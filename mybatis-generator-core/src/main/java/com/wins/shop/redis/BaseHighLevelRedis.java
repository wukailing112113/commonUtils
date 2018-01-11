package com.wins.shop.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class BaseHighLevelRedis<POJO> {

	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void mset(Map<String, String> mapObj, final long seconds) {
		
		redisTemplate.opsForValue().multiSet(mapObj);
		if (seconds > 0) {
			for (String key : mapObj.keySet()) {
				redisTemplate.boundValueOps(key).expire(seconds, TimeUnit.SECONDS);
			}
		}
	}

	/**
	 * @param keyPre
	 * @return
	 */
	public List<Object> mget(String keyPre){
		Set<String> keys= redisTemplate.keys("keyPre*") ;
		List<Object> values = redisTemplate.opsForValue().multiGet(keys) ;
		return values ;
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void set(final String key, final POJO value, final long seconds) {
		redisTemplate.boundValueOps(key).set(JSONObject.fromObject(value).toString());
		if (seconds > 0) {
			redisTemplate.boundValueOps(key).expire(seconds, TimeUnit.SECONDS);
		}
	}

	
	/**
	 * 调用者再根据情况转换成实体对象
	 * 
	 * @param key
	 * @return
	 */
	public JSONObject get(final String key) {
		JSONObject obj = JSONObject.fromObject(redisTemplate.boundValueOps(key).get());
		return obj;
	}

	/**
	 * Hash 操作得一个字段一个字段地拼，这是拼体力啊。
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void hashSet(final String key, final POJO value, final long seconds) {

	}

	public long expire(final String key, final long seconds) {
		long expire = redisTemplate.boundValueOps(key).getExpire();
		return expire;
	}

}
