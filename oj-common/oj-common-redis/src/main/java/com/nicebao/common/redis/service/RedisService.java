package com.nicebao.common.redis.service;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

	//************************ 操作key ***************************

	/**
	 * 判断Key是否存在
	 * @author IhavBB
	 * @date 18:52 2025/2/20
	 * @param key
	 * @return {@link Boolean}
	**/
	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}


	/**
	 * 设置有效时间
	 * @author IhavBB
	 * @date 18:53 2025/2/20
	 * @param key
	 * @param timeout
	 * @return {@link boolean}
	**/
	public boolean expire(final String key, final long timeout) {
		return expire(key, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 设置有效时间
	 * @author IhavBB
	 * @date 18:53 2025/2/20
	 * @param key
	 * @param timeout 过期时间
	 * @param unit 单位
	 * @return {@link boolean}
	**/
	public boolean expire(final String key, final long timeout, final TimeUnit unit) {
		return redisTemplate.expire(key, timeout, unit);
	}

	/**
	 * 获取剩余有效时间
	 * @author IhavBB
	 * @date 18:53 2025/2/20
	 * @param key
	 * @param unit
	 * @return {@link Long}
	**/
	public Long getExpire(final String key, final TimeUnit unit) {
		return redisTemplate.getExpire(key, unit);
	}

	/**
	 * 删除单个对象
	 * @author IhavBB
	 * @date 18:54 2025/2/20
	 * @param key
	 * @return {@link boolean}
	**/
	public boolean deleteObject(final String key) {
		return redisTemplate.delete(key);
	}

	//************************ 操作String类型 ***************************

	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 * @author IhavBB
	 * @date 20:44 2025/2/20
	 * @param key
	 * @param value
	**/
	public <T> void setCacheObject(final String key, final T value) {
		redisTemplate.opsForValue().set(key, value);
	}

	/**
	 *  缓存基本的对象，Integer、String、实体类等
	 * @author IhavBB
	 * @date 20:45 2025/2/20
	 * @param key
	 * @param value
	 * @param timeout
	 * @param timeUnit
	**/
	public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
	}

	/**
	 * 获得缓存的基本对象。
	 * @author IhavBB
	 * @date 20:46 2025/2/20
	 * @param key
	 * @param clazz
	 * @return {@link T}
	**/
	public <T> T getCacheObject(final String key, Class<T> clazz) {
		ValueOperations<String, T> operation = redisTemplate.opsForValue();
		T t = operation.get(key);
		if (t instanceof String) {
			return t;
		}
		return JSON.parseObject(String.valueOf(t), clazz);
	}

	public <T> List<T> multiGet(final List<String> keyList, Class<T> clazz) {
		List list = redisTemplate.opsForValue().multiGet(keyList);
		if (list == null || list.size() <= 0) {
			return null;
		}
		List<T> result = new ArrayList<>();
		for (Object o : list) {
			result.add(JSON.parseObject(String.valueOf(o), clazz));
		}
		return result;
	}

	public <K, V> void multiSet(Map<? extends K, ? extends V> map) {
		redisTemplate.opsForValue().multiSet(map);
	}

	/**
	 * +1
	 * @author IhavBB
	 * @date 21:08 2025/2/20
	 * @param key
	 * @return {@link Long}
	**/
	public Long increment(final String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	//*************** 操作list结构 ****************

	/**
	 * 获取list中存储数据数量
	 * @author IhavBB
	 * @date 21:08 2025/2/20
	 * @param key
	 * @return {@link Long}
	**/
	public Long getListSize(final String key) {
		return redisTemplate.opsForList().size(key);
	}

	/**
	 * 获取list中指定范围数据
	 * @author IhavBB
	 * @date 21:08 2025/2/20
	 * @param key
	 * @param start
	 * @param end
	 * @param clazz
	 * @return {@link List<T>}
	**/
	public <T> List<T> getCacheListByRange(final String key, long start, long end, Class<T> clazz) {
		List range = redisTemplate.opsForList().range(key, start, end);
		if (CollectionUtils.isEmpty(range)) {
			return null;
		}
		return JSON.parseArray(JSON.toJSONString(range), clazz);
	}

	/**
	 * 底层使用list结构存储数据(尾插 批量插入)
	 * @author IhavBB
	 * @date 21:09 2025/2/20
	 * @param key
	 * @param list
	 * @return {@link Long}
	**/
	public <T> Long rightPushAll(final String key, Collection<T> list) {
		return redisTemplate.opsForList().rightPushAll(key, list);
	}

	/**
	 * 底层使用list结构存储数据(头插)
	 * @author IhavBB
	 * @date 21:10 2025/2/20
	 * @param key
	 * @param value
	 * @return {@link Long}
	**/
	public <T> Long leftPushForList(final String key, T value) {
		return redisTemplate.opsForList().leftPush(key, value);
	}

	/**
	 * 底层使用list结构,删除指定数据
	 * @author IhavBB
	 * @date 21:10 2025/2/20
	 * @param key
	 * @param value
	 * @return {@link Long}
	**/
	public <T> Long removeForList(final String key, T value) {
		return redisTemplate.opsForList().remove(key, 1L, value);
	}


	public <T> Long indexOfForList(final String key, T value) {
		return redisTemplate.opsForList().indexOf(key, value);
	}

	public <T> T indexForList(final String key, long index, Class<T> clazz) {
		Object t = redisTemplate.opsForList().index(key, index);
		return JSON.parseObject(String.valueOf(t), clazz);
	}


	//************************ 操作Hash类型 ***************************
	public <T> T getCacheMapValue(final String key, final String hKey, Class<T> clazz) {
		Object cacheMapValue = redisTemplate.opsForHash().get(key, hKey);
		if (cacheMapValue != null) {
			return JSON.parseObject(String.valueOf(cacheMapValue), clazz);
		}
		return null;
	}


	/**
	 * 获取多个Hash中的数据
	 * @author IhavBB
	 * @date 21:10 2025/2/20
	 * @param key
	 * @param hKeys
	 * @param clazz
	 * @return {@link List<T>}
	**/
	public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys, Class<T> clazz) {
		List list = redisTemplate.opsForHash().multiGet(key, hKeys);
		List<T> result = new ArrayList<>();
		for (Object item : list) {
			result.add(JSON.parseObject(JSON.toJSONString(item), clazz));
		}

		return result;
	}

	/**
	 * 往Hash中存入数据
	 * @author IhavBB
	 * @date 21:10 2025/2/20
	 * @param key
	 * @param hKey
	 * @param value
	**/
	public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
		redisTemplate.opsForHash().put(key, hKey, value);
	}

	/**
	 * 缓存Map
	 * @author IhavBB
	 * @date 21:10 2025/2/20
	 * @param key
	 * @param dataMap
	**/
	public <K, T> void setCacheMap(final String key, final Map<K, T> dataMap) {
		if (dataMap != null) {
			redisTemplate.opsForHash().putAll(key, dataMap);
		}
	}

	public Long deleteCacheMapValue(final String key, final String hKey) {
		return redisTemplate.opsForHash().delete(key, hKey);
	}

	public Long incrementHashValue(final String key, final String hKey, long delta) {
		return redisTemplate.opsForHash().increment(key, hKey, delta);
	}
}