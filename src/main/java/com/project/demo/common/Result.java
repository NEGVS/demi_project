package com.project.demo.common;

import com.project.demo.common.enums.CommonCode;
import io.opentelemetry.api.trace.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
// 设置链式数据
@ToString
@Accessors(chain = true)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -7598829391091941077L;

    public static Integer SUCCESS_CODE = 200;

    public static Integer ERROR_CODE = 500;

    public static Integer UNEXPECTED_CODE = -1;


    /**
     * message 可以为空
     */
    @Schema(description = "信息提示")
    private String message;

    /**
     * 返回的code 请勿使用 20000
     */
    @NotNull
    @Schema(description = "状态码", example = "200")
    private Integer code;

    /**
     * 返回的data , 可以为空
     */
    @Schema(description = "返回数据")
    private T data;


    /**
     * @param data 返回的实体
     * @return 成功
     */
    public static <T> Result<T> success(T data) {
        return customize(SUCCESS_CODE, data, null);
    }

    /**
     * @return 成功
     */
    public static <T> Result<T> success() {
        return customize(SUCCESS_CODE, null, "操作成功");
    }

    /**
     * @param message 错误消息
     * @return 错误
     */
    public static <T> Result<T> error(String message) {
        return error(ERROR_CODE, message);
    }
    /**
     * @return 错误
     */
    public static <T> Result<T> error() {
        return error(ERROR_CODE, "操作失败");
    }


    /**
     * @param code    {@link org.springframework.http.HttpStatus} 或者0和1
     * @param message 错误消息
     * @return 错误
     */
    public static <T> Result<T> error(Integer code, String message) {
        return customize(code, null, message);
    }

    /**
     * @param code    {@link org.springframework.http.HttpStatus} 或者0和1
     * @param data    返回泛型对象
     * @param message 返回消息
     *                自定义
     */
    public static <T> Result<T> customize(Integer code, T data, String message) {
        Result<T> result = new Result<>();
        result.setMessage(message);
        result.setCode(code);
        result.setData(data);
        return result;
    }

    /**
     * @param code {@link org.springframework.http.HttpStatus} 或者0和1
     * @param data 返回泛型对象
     *             自定义
     */
    public static <T> Result<T> customize(Integer code, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        return result;
    }

    /**
     * @param code {@link org.springframework.http.HttpStatus} 或者0和1
     *             自定义
     */
    public static <T> Result<T> customize(Integer code) {
        Result<T> result = new Result<>();
        result.setCode(code);
        return result;
    }

    public static <T> Result<T> fail(String message, Integer code) {
        return customize(code, null, message);
    }

    public static <T> Result<T> fail(CommonCode commonCode) {
        return customize(commonCode.getCode(), null, commonCode.getDesc());
    }
    /**
     * 判断当前 {@link Result} 是否成功
     *
     * @return true 表示成功
     */
    public boolean izSuccess() {
        return Objects.equals(this.code, SUCCESS_CODE);
    }

}
