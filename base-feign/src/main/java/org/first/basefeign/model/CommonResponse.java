package org.first.basefeign.model;

/**
 * @since 2025/11/28
 * @description 通用返回类型
 * */
public class CommonResponse<T> {

    private int code;
    private String message;
    private T data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public boolean isSuccess() { return this.code == 0; }

}
