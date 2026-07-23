package com.yanque.commons.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

@Component
public class RedisUtils{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, Duration timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // setnx()
    public Boolean setIfAbsent(String key, String value, Duration timeout) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout);
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /** 将值记录到 Redis Set，用于维护会话 nonce 或用户权限编码集合。 */
    public Long addToSet(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    public Set<String> getSetMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /** Redis SISMEMBER：判断集合中是否存在指定成员。 */
    public Boolean isSetMember(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    public Boolean expire(String key, Duration timeout) {
        return stringRedisTemplate.expire(key, timeout);
    }

    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }
}
