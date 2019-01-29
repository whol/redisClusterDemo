package com.example.demo.common.exception.http;

import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-3-2.
 */
public class ErrorServletRequestFacade extends HttpServletRequestWrapper {

    private String _contentType = MediaType.TEXT_HTML_VALUE;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public ErrorServletRequestFacade(String contentType, HttpServletRequest request) {
        super(request);
        this._contentType = contentType;
    }

    public String getContentType() {
        return this._contentType;
    }

    public String getHeader(String headerName) {
        if (headerName != null && headerName.equalsIgnoreCase("content-type"))
            return getContentType();
        else {
            return super.getHeader(headerName);
        }
    }

    public String getRequestURI() {
        return getOriginalRequestURI();
    }

    public String getOriginalRequestURI() {
        return getRequestURI((HttpServletRequest)this.getRequest());
    }

    private String getRequestURI(HttpServletRequest request) {
        if (request instanceof HttpServletRequestWrapper) {
            return getRequestURI((HttpServletRequest)((HttpServletRequestWrapper)request).getRequest());
        } else {
            return request.getRequestURI();
        }
    }

}
