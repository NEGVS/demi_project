package com.project.demo.common.secuity;

import com.project.demo.common.JSONAuthentication;
import com.project.demo.common.Result;
import com.project.demo.common.enums.CommonCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败处理类（用户的认证信息未存入security的上下文中）
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint extends JSONAuthentication implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("认证失败...");
        Result<String> result;
        if (authException instanceof AccountExpiredException) {
            //账号过期
            result = Result.fail(CommonCode.ACCOUNT_EXPIRED);
        } else if (authException instanceof InternalAuthenticationServiceException) {
            //用户不存在
            result = Result.fail(CommonCode.USER_NOT_FOUND);
        } else if (authException instanceof BadCredentialsException) {
            //用户名或密码错误（也就是用户名匹配不上密码）
            result = Result.fail(CommonCode.INVALID_CREDENTIALS);
        } else if (authException instanceof CredentialsExpiredException) {
            //密码过期
            result = Result.fail(CommonCode.PASSWORD_EXPIRED);
        } else if (authException instanceof DisabledException) {
            //账号不可用
            result = Result.fail(CommonCode.ACCOUNT_DISABLED);
        } else if (authException instanceof LockedException) {
            //账号锁定
            result = Result.fail(CommonCode.ACCOUNT_LOCKED);
        } else if (authException instanceof InsufficientAuthenticationException) {
            //权限不足
            result = Result.fail(CommonCode.INSUFFICIENT_PERMISSIONS);
        } else {
            //其他错误
            result = Result.fail(CommonCode.OTHER_ERROR);
        }
        //输出
        this.WriteJSON(response, result);
    }
}
