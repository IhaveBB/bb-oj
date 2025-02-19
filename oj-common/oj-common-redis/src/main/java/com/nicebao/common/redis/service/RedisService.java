package com.nicebao.common.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * RedisService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@Component
public class RedisService {
	@Autowired
	public RedisTemplate redisTemplate;

	public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
	}
}
