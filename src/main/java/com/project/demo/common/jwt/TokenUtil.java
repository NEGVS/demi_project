package com.project.demo.common.jwt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.project.demo.code.domain.common.MyUserDetails;
import com.project.demo.common.CacheConstants;
import com.project.demo.common.enums.RedisKey;
import com.project.demo.common.util.RedisUtil;
import com.project.demo.common.util.IpUtil;
import com.project.demo.common.util.ServletUtil;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.project.demo.common.CacheConstants.LOGIN_TOKEN_KEY;

@Component
@Slf4j
public class TokenUtil {

    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private static String secret;

    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 30 分钟
     */
    public static final Long MILLIS_MINUTE_TEN = 30 * 60 * 1000L;
    private static final Key SECRET_KEY = generateKeyFromString("hylogan-dai");
    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    @Resource
    private RedisUtil redisUtil;

    public static Key generateKeyFromString(String input) {
        byte[] keyBytes = input.getBytes(StandardCharsets.UTF_8);

        // 如果长度不足 32 字节，填充到 32 字节
        if (keyBytes.length < 32) {
            byte[] paddedKey = Arrays.copyOf(keyBytes, 32);
            for (int i = keyBytes.length; i < 32; i++) {
                paddedKey[i] = '*'; // 填充字符
            }
            keyBytes = paddedKey;
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 从 token 中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从 token 中提取ID
    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }

    // 从 token 中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 从 token 中提取特定的声明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有声明
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    /**
     * 生成 Token 没有过期时间
     *
     * @return token
     */
    public String generateToken(MyUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(RedisKey.LOGIN_USER_KEY.getPrefix(), userDetails.getToken());
        return createToken(claims, userDetails.getUsername(), userDetails.getToken(), null);
    }

    /**
     * 验证令牌有效期，相差不足30分钟，自动刷新缓存
     *
     * @param loginUser 用户信息
     */
    public void verifyToken(MyUserDetails loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(MyUserDetails loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        // 令牌有效期
        int expireTime = 30;
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisUtil.setWithExpire(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public MyUserDetails getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StrUtil.isNotEmpty(token)) {
            try {
                Claims claims = extractAllClaims(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(RedisKey.LOGIN_USER_KEY.getPrefix());
                Object object = redisUtil.get(CacheConstants.LOGIN_TOKEN_KEY + uuid);
                if (object != null) {
                    // 转换成 MyUserDetails
                    return JSONUtil.toBean(JSONUtil.toJsonStr(object), MyUserDetails.class);
                }
            } catch (Exception e) {
                log.error("获取用户信息异常'{}'", e.getMessage());
            }
        }
        return null;
    }

    /**
     * @param uuid 用户唯一标识
     * @return key
     */
    private String getTokenKey(String uuid) {
        return LOGIN_TOKEN_KEY + uuid;
    }


    // 检查 token 是否过期
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // 生成访问 token，有效期 30 分钟
    public String generateAccessToken(MyUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), userDetails.getToken(), 1000 * 60 * 30L);
    }

    // 生成刷新 token，有效期 7 天
    public String generateRefreshToken(MyUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), userDetails.getToken(), 1000 * 60 * 60 * 24 * 7L);
    }

    /**
     * 创建token
     *
     * @param claims         自定义声明
     * @param subject        主题
     * @param id             用户唯一ID
     * @param expirationTime 过期时间
     * @return token
     */
    private String createToken(Map<String, Object> claims, String subject, String id, Long expirationTime) {
        JwtBuilder builder = Jwts.builder();
        // 添加自定义声明
        builder.setClaims(claims);
        // 添加主题
        builder.setSubject(subject);
        // 添加签发时间
        builder.setIssuedAt(new Date(System.currentTimeMillis()));
        // 添加ID
        builder.setId(String.valueOf(id));
        if (expirationTime != null) {
            // 设置过期时间
            builder.setExpiration(new Date(System.currentTimeMillis() + expirationTime));
        }
        // 签名
        return builder.signWith(SECRET_KEY).compact();
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(MyUserDetails loginUser) {
        String token = UUID.randomUUID().toString();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(RedisKey.LOGIN_USER_KEY.getPrefix(), token);
        return createToken(claims);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SECRET_KEY).compact();
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(MyUserDetails loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getRequest().getHeader("User-Agent"));
        String ip = IpUtil.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(IpUtil.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    // 验证 token
    public Boolean validateToken(String token, MyUserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 刷新 token 过期时间
    public String refreshTokenExpiration(String token, Long newExpirationTime) {
        Claims claims = extractAllClaims(token);
        claims.setExpiration(new Date(System.currentTimeMillis() + (newExpirationTime == null ? 1000 * 60 * 30L : newExpirationTime)));
        return Jwts.builder().setClaims(claims).signWith(SECRET_KEY).compact();
    }
    /**
     * 获取request中的token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StrUtil.isNotBlank(token)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }
}
