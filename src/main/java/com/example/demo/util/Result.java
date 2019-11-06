package com.example.demo.util;

/**
 * Created by Administrator on 2017/6/5.
 */
public class Result<T> {
    //代码
    private int code;
    //提示信息
    private String message;
    //具体内容
    private T data;

    // 是否成功
    private boolean success;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Result(T data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public Result() {
    }
}
