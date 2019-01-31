package com.example.demo.common.converter;


import com.example.demo.common.exception.GeneralException;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-8-9.
 */
public class ConvertException extends GeneralException {

    public ConvertException(String errorCode) {
        super(errorCode);
    }

    public ConvertException(String errorCode, String message) {
        super(errorCode, message);
    }

    public ConvertException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ConvertException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
