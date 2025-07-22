package com.project.demo.common.util;

import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${user.login.key}")
    @Getter
    private String userLogin;

    /**
     * 新增一个键值对
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }

    /**
     * 新增一个键值对，并设置过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeout, unit);
    }
    /**
     * 删除一个键值对
     *
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置键的过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 检查键是否过期
     *
     * @param key 键
     * @return true 如果键存在并且未过期，否则返回false
     */
    public boolean isExpired(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire == null || expire <= 0;
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

    /**
     * 检验一个键是否存在，并判断是否因为过期而删除
     *
     * @param key 键
     * @return true 如果键从未存在，false 如果键过期或已删除
     */
    public boolean isKeyNonExistent(String key) {
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey == null || !hasKey;
    }
}