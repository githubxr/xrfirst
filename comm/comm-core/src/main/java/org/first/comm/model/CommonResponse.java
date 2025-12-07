package org.first.comm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.first.comm.constant.CommonConstant;

/**
 * @since 2025/11/28
 * @description 通用返回类型
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {

    private int code;
    private boolean success;
    private String message;
    private T data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public boolean isSuccess() { return this.code == 0; }

    //快捷成功
    public static<T> CommonResponse<T> success(String message) {
        CommonResponse<T> r = new CommonResponse<T>();
        r.message = message;
        r.code = CommonConstant.SC_OK_200;
        r.success = true;
        return r;
    }
    //快捷失败: 默认500
    public static<T> CommonResponse<T> error(String msg, T data) {
        CommonResponse<T> r = new CommonResponse<T>();
        r.setSuccess(false);
        r.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
        r.setMessage(msg);
        r.setData(data);
        return r;
    }
}
