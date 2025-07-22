package com.project.demo.common.exception;

import cn.hutool.core.util.StrUtil;
import com.project.demo.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalException extends RuntimeException {

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.info("异常：",e.getCause());
        log.info("请求：" + requestURI + e.getMessage() + "；异常：{}", e.getMessage());
        return Result.error(StrUtil.isNotBlank(e.getMessage()) ? e.getMessage() : "系统繁忙！");
    }
}
