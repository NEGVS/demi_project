package com.project.demo.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {
    // 定义用于签名的密钥
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 定义令牌的过期时间（1小时，3600000毫秒）
    private static final long EXPIRATION_TIME = 3600000;

    // 生成JWT令牌
    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // 从JWT令牌中提取用户名
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 验证JWT令牌
    public static boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 提取单个声明
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有声明
    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    // 检查令牌是否过期
    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 提取令牌的过期时间
    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 创建JWT令牌
    private static String createToken(Map<String, Object> claims, String subject) {
        //设置头信息
        Map<String, Object> header = new HashMap();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 获取令牌的剩余有效期
    public static long getRemainingTime(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.getTime() - System.currentTimeMillis();
    }

    // 刷新令牌
    public static String refreshToken(String token) {
        final Claims claims = extractAllClaims(token);
        return createToken(claims, claims.getSubject());
    }

    // 刷新令牌过期时间但不更换token
    public static String refreshTokenExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.getSubject())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}
