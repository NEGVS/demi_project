package com.project.demo.common.secuity;

import com.project.demo.common.JSONAuthentication;
import com.project.demo.common.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 鉴权失败(用户没有权限访问)
 */
@Component
@Slf4j
public class JwtAccessDeniedHandler extends JSONAuthentication implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("鉴权失败...");
        this.WriteJSON(response, Result.fail("权限不足",50001));
    }
}
