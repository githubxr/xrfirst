package org.first.comm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.first.comm.constant.CommonConstant;

/**
 * 项目统一返回体
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private int code;
    private boolean success;
    private String message;
    private T data;

    public boolean isSuccess() { return this.code == CommonConstant.SC_OK_200; }

    // ===== success helpers =====
    public static <T> CommonResponse<T> success(String message, T data) {
        CommonResponse<T> r = new CommonResponse<>();
        r.code = CommonConstant.SC_OK_200;
        r.success = true;
        r.message = message;
        r.data = data;
        return r;
    }
    public static <T> CommonResponse<T> success(String message) {
        return success(message, null);
    }
    public static <T> CommonResponse<T> success(T data) {
        return success(null, data);
    }

    // ===== error helpers =====
    public static <T> CommonResponse<T> error(String message, T data) {
        CommonResponse<T> r = new CommonResponse<>();
        r.code = CommonConstant.SC_INTERNAL_SERVER_ERROR_500;
        r.success = false;
        r.message = message;
        r.data = data;
        return r;
    }
    public static <T> CommonResponse<T> error(String message) {
        return error(message, null);
    }
}
