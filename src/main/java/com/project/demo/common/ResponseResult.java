package com.project.demo.common;

import lombok.Data;

@Data
public class ResponseResult<T> {

    /**
     * 状态码
     */
    private Integer code = 200;

    /**
     * 状态信息
     */
    private Boolean status = true;

    /**
     * 返回信息
     */
    private String message = "success";

    /**
     * 数据
     */
    private T data;

    public static <T> ResponseResult<T> response(T data) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(200);
        responseResult.setStatus(true);
        responseResult.setMessage("success");
        responseResult.setData(data);
        return responseResult;
    }

    public static <T> ResponseResult<T> success(T data) {
        return response(200, true, "123", data);
    }

    private static <T> ResponseResult<T> response(Integer code, Boolean status, String message, T data) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setStatus(status);
        responseResult.setMessage(message);
        responseResult.setData(data);
        return responseResult;
    }

}
