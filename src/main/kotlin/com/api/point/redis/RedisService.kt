package com.api.point.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service


@Service
class RedisService(
    private val redisTemplate: StringRedisTemplate
) {
    // set
    fun save(key: String, value: String) {
        val setOperations = redisTemplate.opsForValue()
        setOperations.set(key, value);
    }

    // set
    fun find(key: String): String? {
        val getOperations = redisTemplate.opsForValue()
        return getOperations.get(key)
    }
}