package com.wins.shop.redis;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;


public class BaseLowLevelRedis{

	@Autowired
	protected RedisTemplate<Serializable, Serializable> redisTemplate;

	public void set(final Serializable key, final Serializable value, final long seconds) {
		redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection conn) throws DataAccessException {
				byte[] keyBytes = RedisUtil.serialize(key);
				byte[] valueBytes = RedisUtil.serialize(value);
				conn.set(keyBytes, valueBytes);
				if(seconds > 0){
					conn.expire(keyBytes, seconds) ;
				}
				return true ;
			}
		});
	}

	public Serializable get(final Serializable key) {
		return redisTemplate.execute(new RedisCallback<Serializable>() {
			public Serializable doInRedis(RedisConnection conn) throws DataAccessException {
				byte[] keyBytes = RedisUtil.serialize(key);
				byte[] valueBytes = conn.get(keyBytes) ;
				return (Serializable)RedisUtil.deserialize(valueBytes) ;
			}
		});
	}

	public int expire(final Serializable key, final long seconds) {
		// TODO Auto-generated method stub
		return 0;
	}


}
