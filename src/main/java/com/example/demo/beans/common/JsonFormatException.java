package com.example.demo.beans.common;


import com.example.demo.common.exception.GeneralException;

/**
 * JSON格式化异常
 * @see GeneralException
 * Created by alen on 17-9-21.
 */
public class JsonFormatException extends GeneralException {

    public JsonFormatException(String errorCode) {
        super(errorCode);
    }

    public JsonFormatException(String errorCode, String message) {
        super(errorCode, message);
    }

    public JsonFormatException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public JsonFormatException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
