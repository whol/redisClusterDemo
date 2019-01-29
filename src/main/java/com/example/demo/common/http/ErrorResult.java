package com.example.demo.common.http;

/**
 * 错误返回结果对象
 * Created by Zav Deng/dengzf@asiainfo.com on 17-3-2.
 */
public class ErrorResult {

    private String returnCode;
    private String returnMessage;
    private Throwable error;
    private String path;

    /***
     * @param returnCode 返回码
     * @param returnMsg 错误描述
     * @param path 错误请求路径
     * @param error 异常对象
     * */
    public ErrorResult(String returnCode, String returnMsg, String path, Throwable error) {
        this.returnCode = returnCode;
        this.returnMessage = returnMsg;
        this.path = path;
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }
}
