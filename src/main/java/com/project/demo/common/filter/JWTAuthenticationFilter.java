package com.project.demo.common.filter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.project.demo.common.Result;
import com.project.demo.common.jwt.TokenUtil;
import com.project.demo.common.util.RedisUtil;
import com.project.demo.code.domain.common.MyUserDetails;
import com.project.demo.code.service.UserService;
import com.project.demo.common.util.SecurityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserService userService;

    @Resource
    private TokenUtil tokenUtil;

    @Value("${jwt.isTest}")
    private boolean isTest;

    List<String> filterList = List.of("/futures", "/doc.html", "/webjars", "/v3/api-docs", "/authority/login", "/authority/register");

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 获取请求的 URI 和方法
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 允许的公开 URI
        if (filterList.contains(uri) || isPublicUri(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 记录请求信息
        log.info("请求地址IP：[" + request.getRemoteAddr() + "]; 请求方式：[" + method + "]; 请求路径：" + uri);

        if (isTest) {
            // 获取用户信息
            MyUserDetails userDetails = userService.loadUserByUsername("admin");
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);
            return;
        }
        // 从redis中获取用户信息
        MyUserDetails loginUser = tokenUtil.getLoginUser(request);

        if (  loginUser != null && SecurityUtils.getAuthentication() == null )
        {
            // 刷新用户token过期时间
            tokenUtil.verifyToken( loginUser );

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( loginUser, null, loginUser.getAuthorities() );

            authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
            SecurityContextHolder.getContext().setAuthentication( authenticationToken );
        }
        filterChain.doFilter(request, response);
    }

    // 检查 URI 是否是公开的
    private boolean isPublicUri(String uri) {
        return uri.startsWith("/futures") || uri.contains("/doc.html") || uri.contains("/webjars") || uri.contains("/v3/api-docs") || uri.contains("/authority/login") || uri.contains("/authority/register");
    }
}
