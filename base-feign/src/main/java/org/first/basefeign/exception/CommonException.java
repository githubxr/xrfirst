package org.first.basefeign.exception;

/**
 * @description 统一配置：feign调用方抛出的业务异常
 * @since 2025/11/28
 * */
public class CommonException extends RuntimeException {

    private int code;
    private String message;

    public CommonException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
