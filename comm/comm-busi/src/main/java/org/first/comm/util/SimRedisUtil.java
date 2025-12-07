package org.first.comm.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @description 临时编写的简单redis工具
 * @since 2025/12/07
 *
 * */
@Component
public class SimRedisUtil {
    // 注入 StringRedisTemplate（专用于字符串操作，序列化更友好）
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 示例：String 类型 CRUD
    public void setKey(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value, 300); // 5分钟过期
    }

    public String getKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // 示例：Hash 类型操作
    public void hset(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }
}