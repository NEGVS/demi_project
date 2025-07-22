package com.project.demo.common;

import cn.hutool.core.util.ObjectUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Slf4j
public class JwtUtil {
    /**
     * 注入header值
     */
    @Value("${token.header}")
    private String header;

    /**
     * 注入secret密钥
     */
    @Value("${token.secret}")
    private String SECRET;


    /**
     * 生成token
     *
     * @param phone 手机号码
     * @return 返回token
     */
    public String generateToken(String phone) {
        Calendar instance = Calendar.getInstance();
        // 设置过期时间
        instance.add(Calendar.SECOND, 1000);
        return Jwts.builder()
                .setSubject(phone)//主题
                .setIssuedAt(new Date(System.currentTimeMillis()))//签发日期
                .setExpiration(instance.getTime())// 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 获取信息
     *
     * @param request 信息来源
     * @return 返回信息
     */
    public String getLoginUser(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (Objects.isNull(token) || ObjectUtil.isEmpty(token)) {
            return null;
        }
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        // 解析对应的权限以及用户信息
        return claims.get("sub").toString();
    }

    /**
     * 检查token是否过期
     *
     * @param token token
     * @return boolean
     */
    public boolean isExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.getExpiration().before(new Date());
    }

    /**
     * 校验token是否符合规则
     *
     * @param authToken token
     * @return boolean
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("签名异常，Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("格式错误，Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("过期的token，JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的token，JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("非法数据异常，JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
