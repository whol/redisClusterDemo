package com.example.demo.common.json;

/**
 * Created by alen on 17-11-2.
 */
public class JsonMappingException extends JsonException {

    public JsonMappingException() {
    }

    public JsonMappingException(String message) {
        super(message);
    }

    public JsonMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonMappingException(Throwable cause) {
        super(cause);
    }
}
