package com.project.demo.common.handler;

import com.project.demo.common.Result;
import com.project.demo.common.exception.DemoProjectException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DemoProjectException.class)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.info("请求" + requestURI + e.getMessage());
        return Result.error(e.getMessage());
    }
}
