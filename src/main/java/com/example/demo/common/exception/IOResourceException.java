package com.example.demo.common.exception;


/**
 * I/O资源操作异常基础类（如：网络I/O，文件I/O等）
 *
 * @author Zifeng.D 2016-12-02 创建
 * @since 0.0.1
 */
public class IOResourceException extends GeneralException {

    public IOResourceException(String errorCode) {
        super(errorCode);
    }

    public IOResourceException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public IOResourceException(String errorCode, String message) {
        super(errorCode, message);
    }

    public IOResourceException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public IOResourceException(String errorCode, String message, String provinceCode, Throwable cause) {
        super(errorCode, message, provinceCode, cause);
    }

    public IOResourceException(String errorCode, String message, String provinceCode) {
        super(errorCode, message, provinceCode);
    }
}
