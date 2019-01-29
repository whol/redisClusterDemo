package com.example.demo.common.http;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.resource.PathResourceResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 处理请求资源定位
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-3-7.
 */
public abstract class ResourceLocator {

    private static final Logger LOGGER = Logger.getLogger(ResourceLocator.class);

    public static final String DEFAULT_WELCOME_PAGE = "index.html";

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/public/" };

    private static final Class<PathExtensionContentNegotiationStrategy> MEDIATYPE_RESOLVER_CLASS = PathExtensionContentNegotiationStrategy.class;

    private List<String> locations = new ArrayList<String>(4);

    private ContentNegotiationManager contentNegotiationManager;

    private PathResourceResolver resourceResolver = new PathResourceResolver();

    private final ContentNegotiationManagerFactoryBean cnmFactoryBean = new ContentNegotiationManagerFactoryBean();

    private PathExtensionContentNegotiationStrategy mediaTypeResolver;

    private final ExtendedFileTypeNegotiationStrategy extendedMediaTypeResolver;

    public ResourceLocator() {
        this.cnmFactoryBean.afterPropertiesSet();
        this.contentNegotiationManager = this.cnmFactoryBean.getObject();
        this.contentNegotiationManager.getStrategies().add(new ExtendedFileTypeNegotiationStrategy());
        this.setLocations(CLASSPATH_RESOURCE_LOCATIONS);

        this.mediaTypeResolver = this.contentNegotiationManager.getStrategy(PathExtensionContentNegotiationStrategy.class);
        this.extendedMediaTypeResolver = this.contentNegotiationManager.getStrategy(ExtendedFileTypeNegotiationStrategy.class);
    }

    public boolean writeRequestResource(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
        Resource resource = getResource(request, uri);
        if (resource != null) {
            MediaType mediaType = getMediaType(request, resource);
            if (mediaType != null) {
                response.setContentType(mediaType.getType());
            } else {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            }

            ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);

            ResourceHttpMessageConverter messageConverter = new ResourceHttpMessageConverter();
            messageConverter.write(resource, mediaType, outputMessage);
            return true;
        }
        return false;
    }

    public boolean writeRequestResource(HttpServletRequest request, HttpServletResponse response) throws IOException{
        return writeRequestResource(request, response, "");
    }

    public void setLocations(String[] locations) {
        Assert.notNull(locations, "Locations list must not be null");
        this.locations.clear();
        this.locations.addAll(Arrays.asList(locations));
    }

    public List<Resource> getLocations() {
        List<Resource> resources = new ArrayList<>();
        for (String location : this.locations) {
            resources.add(getResourceLoader().getResource(location));
        }
        return resources;
    }

    public Resource getResource(HttpServletRequest request, String uri) throws IOException {
        String path = StringUtils.isEmpty(uri) ? request.getRequestURI() : uri;
        String contextPath = request.getContextPath();

        path = processPath(getRealPath(contextPath, path));

        if (!StringUtils.hasText(path) || isInvalidPath(path)) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ignoring invalid resource path [" + path + "]");
            }
            return null;
        }

        if (path.contains("%")) {
            try {
                if (isInvalidPath(URLDecoder.decode(path, "UTF-8"))) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Ignoring invalid resource path with escape sequences [" + path + "].");
                    }
                    return null;
                }
            }
            catch (IllegalArgumentException ex) {
                // ignore
            }
        }
        return resourceResolver.resolveResource(request, path, getLocations(), null);
    }

    public Resource getResource(HttpServletRequest request) throws IOException {
        return getResource(request, "");
    }

    private String getRealPath(String contextPath, String uri) {
        if (contextPath == null && "".equals(contextPath))
            return getRealPath("/", uri);
        else {
            if ("/".equals(contextPath))
                return uri;
            else {
                return uri.replaceFirst(contextPath, "");
            }
        }
    }

    protected String processPath(String path) {
        boolean slash = false;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                slash = true;
            }
            else if (path.charAt(i) > ' ' && path.charAt(i) != 127) {
                if (i == 0 || (i == 1 && slash)) {
                    return path;
                }
                path = slash ? "/" + path.substring(i) : path.substring(i);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Path after trimming leading '/' and control characters: " + path);
                }
                return path;
            }
        }
        return (slash ? "/" : "");
    }

    protected boolean isInvalidPath(String path) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Applying \"invalid path\" checks to path: " + path);
        }
        if (path.contains("WEB-INF") || path.contains("META-INF")) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Path contains \"WEB-INF\" or \"META-INF\".");
            }
            return true;
        }
        if (path.contains(":/")) {
            String relativePath = (path.charAt(0) == '/' ? path.substring(1) : path);
            if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Path represents URL or has \"url:\" prefix.");
                }
                return true;
            }
        }
        if (path.contains("..")) {
            path = StringUtils.cleanPath(path);
            if (path.contains("../")) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Path contains \"../\" after call to StringUtils#cleanPath.");
                }
                return true;
            }
        }
        return false;
    }

    protected MediaType getMediaType(HttpServletRequest request, Resource resource) {
        MediaType mediaType = null;

        if (mediaTypeResolver != null) {
            mediaType = mediaTypeResolver.getMediaTypeForResource(resource);
        }

        if (mediaType == null && extendedMediaTypeResolver != null) {
            mediaType = extendedMediaTypeResolver.getMediaTypeForResource(resource);
        }

        if (mediaType == null) {
            ServletWebRequest webRequest = new ServletWebRequest(request);
            try {
                List<MediaType> mediaTypes = getContentNegotiationManager().resolveMediaTypes(webRequest);
                if (!mediaTypes.isEmpty()) {
                    mediaType = mediaTypes.get(0);
                }
            } catch (HttpMediaTypeNotAcceptableException ex) {
                // Ignore
            }
        }
        return mediaType;
    }

    public ContentNegotiationManager getContentNegotiationManager() {
        return contentNegotiationManager;
    }

    public abstract ResourceLoader getResourceLoader();


}
