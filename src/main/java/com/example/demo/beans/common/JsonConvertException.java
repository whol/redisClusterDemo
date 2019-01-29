package com.example.demo.beans.common;

import com.cmos.common.exception.GeneralException;

/**
 * Created by alen on 17-9-27.
 */
public class JsonConvertException extends GeneralException {

    public JsonConvertException(String errorCode, String message) {
        super(errorCode, message);
    }

    public JsonConvertException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
