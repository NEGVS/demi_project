package com.project.demo.code.controller.test;

import com.project.demo.common.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "JWT相关接口", description = "JWT相关接口")
@Slf4j
@RestController
@RequestMapping("/jwt")
public class JwtController {

    @GetMapping("/generateToken")
    @Operation(summary = "生成token")
    public String generateToken(@RequestParam String username) {
        return JwtUtil.generateToken(username);
    }

    @GetMapping("/validateToken")
    @Operation(summary = "验证token")
    public boolean validateToken(@RequestParam String token, @RequestParam String username) {
        return JwtUtil.validateToken(token, username);
    }

    @GetMapping("/getRemainingTime")
    @Operation(summary = "获取token剩余时间")
    public long getRemainingTime(@RequestParam String token) {
        return JwtUtil.getRemainingTime(token);
    }

    @GetMapping("/refreshToken")
    @Operation(summary = "刷新token")
    public String refreshToken(@RequestParam String token) {
        return JwtUtil.refreshToken(token);
    }

    // 从JWT令牌中提取用户名
    @GetMapping("/extractUsername")
    @Operation(summary = "从JWT令牌中提取用户名")
    public String extractUsername(@RequestParam String token) {
        return JwtUtil.extractUsername(token);
    }

    // 提取单个声明
    @GetMapping("/extractClaim")
    @Operation(summary = "提取单个声明")
    public String extractClaim(@RequestParam String token, @RequestParam String claimType) {
        if ("subject".equalsIgnoreCase(claimType)) {
            return JwtUtil.extractClaim(token, Claims::getSubject);
        }
        return "Unsupported claim type";
    }

    @GetMapping("/extractAllClaims")
    @Operation(summary = "提取所有声明")
    public Claims extractAllClaims(@RequestParam String token) {
        return JwtUtil.extractAllClaims(token);
    }

    // 刷新令牌过期时间但不更换token
    @GetMapping("/refreshTokenExpiration")
    @Operation(summary = "刷新token过期时间但不更换token")
    public String refreshTokenExpiration(@RequestParam String token) {
        return JwtUtil.refreshTokenExpiration(token);
    }
}