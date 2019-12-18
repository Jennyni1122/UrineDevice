package com.leo.device.bean.response;

/**
 * @author Leo
 * @date 2019-05-22
 */
public class BaseResponse<D> {
    private int code;
    private String message;
    private D data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public D getData() {
        return data;
    }
}
