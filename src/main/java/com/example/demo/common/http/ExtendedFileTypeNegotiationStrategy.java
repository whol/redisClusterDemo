package com.example.demo.common.http;

import org.springframework.http.MediaType;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 扩展文件类型的MimeType映射配置类
 *
 * Created by Zav Deng/dengzf@asiainfo.com on 17-4-18.
 */
public class ExtendedFileTypeNegotiationStrategy extends PathExtensionContentNegotiationStrategy {

    public ExtendedFileTypeNegotiationStrategy(Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
    }

    public ExtendedFileTypeNegotiationStrategy() {
        this(new HashMap<String, MediaType>() {{
            // 添加 web font mimetype的支持
            put("woff", MediaType.parseMediaType("application/font-woff"));
            put("woff2", MediaType.parseMediaType("application/font-woff2"));
            put("ttf", MediaType.parseMediaType("application/x-font-ttf"));
            put("eot", MediaType.parseMediaType("application/vnd.ms-fontobject"));
            put("svg", MediaType.parseMediaType("image/svg+xml"));
            put("otf", MediaType.parseMediaType("application/x-font-opentype"));    
        }});
    }
}
