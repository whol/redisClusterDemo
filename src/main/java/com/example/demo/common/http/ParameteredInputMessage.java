package com.example.demo.common.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-11-1.
 */
public class ParameteredInputMessage implements HttpInputMessage {

    private InputStream cachedInputStream;

    private HttpInputMessage inputMessage;

    public ParameteredInputMessage(HttpInputMessage inputMessage, Charset charset) throws IOException {
        this.inputMessage = inputMessage;
        this.cachedInputStream = new ByteArrayInputStream(convertKeyValuePairs(this.consumeStream(inputMessage), charset));
    }

    private byte[] convertKeyValuePairs(byte[] data, Charset charset) throws UnsupportedEncodingException {
        String postContent = new String(data, charset).trim();
        if (isJsonObject(postContent) || isJsonArray(postContent)) {
            return data;
        } else {
            JSONObject body = new JSONObject();
            String[] pairs = postContent.split("&+");
            for (String pair : pairs) {
                if (StringUtils.isBlank(pair)) continue;
                String[] parts = pair.split("=");
                switch (parts.length) {
                    case 1:
                        body.put(parts[0], "");
                        break;
                    case 2:
                        body.put(parts[0], URLDecoder.decode(parts[1], charset.name()));
                        break;
                    default:
                        break;
                }
            }
            return body.toString().getBytes(charset);
        }
    }

    private boolean isJsonObject(String postContent) {
        return postContent.startsWith("{") && postContent.endsWith("}");
    }

    private boolean isJsonArray(String postContent) {
        return postContent.startsWith("[") && postContent.endsWith("]");
    }

    private byte[] consumeStream(HttpInputMessage inputMessage) throws IOException {
        try (InputStream input = inputMessage.getBody()) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                IOUtils.copy(input, out);
                return out.toByteArray();
            }
        }
    }

    @Override
    public InputStream getBody() throws IOException {
        return cachedInputStream;
    }

    @Override
    public HttpHeaders getHeaders() {

        return inputMessage.getHeaders();
    }
}
