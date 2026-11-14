package com.example.scss.config;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "成功";
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = msg;
        r.data = null;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }
}

