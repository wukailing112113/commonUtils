package com.wins.shop.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.wins.shop.redis.PojoSerializable;
import com.wins.shop.util.CommonUtil;

public abstract class BaseRedis<K, V> {
	
	// 加载数据的时候每页加载1000条，免得数据库超时
    protected final Integer LOAD_PAGE_SIZE = 1000 ;
	@Autowired
	protected RedisTemplate<K, V> redisTemplate;
	@Autowired
	protected RedisTemplate<K, V> transRedisTemplate;

	// @Autowired
	// protected Jedis jedis;

	/////////////////////////////////////////////////////////////////
	/////////////////////////// 普通通用接口////////////////////////////
	/////////////////////////////////////////////////////////////////
	/**
	 * 设置一个结构数据
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	protected Long incr(final String key) {
		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				return connection.incr(byteKey);
			}
		});
		return result;
	}

	/**
	 * 设置一个结构数据
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	protected Long decr(final String key) {
		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				return connection.decr(byteKey);
			}
		});
		return result;
	}

	/**
	 * 设置一个结构数据
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	protected boolean set(final String key, final String value, final long seconds) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteValue = serializer.serialize(value);
				if (seconds > 0) {
					connection.setEx(byteKey, seconds, byteValue);
				} else {
					connection.set(byteKey, byteValue);
				}
				return true;
			}
		});
		return result;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @param suppressTran 是否压制事务，true，表示在事务环境中立即执行（不进redis队列）
	 * @return
	 */
	protected boolean set(final String key, final String value, final long seconds, final boolean suppressTran) {
		RedisTemplate<K, V> realTemp = suppressTran?redisTemplate:transRedisTemplate;
		boolean result = realTemp.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteValue = serializer.serialize(value);
				if (seconds > 0) {
					connection.setEx(byteKey, seconds, byteValue);
				} else {
					connection.set(byteKey, byteValue);
				}
				return true;
			}
		});
		return result;
	}

	/**
	 * 设置一个结构数据(主要用于保存bean对象，初始化incr值请使用String类型的方法)
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	protected boolean set(final String key, final Serializable value, final long seconds) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteValue = CommonUtil.transObj2ByteArray(value);
				if (seconds > 0) {
					connection.setEx(byteKey, seconds, byteValue);
				} else {
					connection.set(byteKey, byteValue);
				}
				return true;
			}
		});
		return result;
	}

	/**
	 * 根据Key获取对象
	 * 
	 * @param key
	 * @return
	 */
	protected Object get(final String key) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] value = connection.get(byteKey);
				if (value == null) {
					return null;
				}
				Object dataObj = serializer.deserialize(value);
				return dataObj;
			}
		});
		return result;
	}

	/**
	 * 根据Key获取对象
	 * 
	 * @param key
	 * @return
	 */
	protected <T extends Serializable> T get(final String key, final Class<T> clazz) {
		T result = redisTemplate.execute(new RedisCallback<T>() {
			public T doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] value = connection.get(byteKey);
				if (value == null) {
					return null;
				}
				T dataObj = CommonUtil.transByteArray2Obj(value, clazz);
				return dataObj;
			}
		});
		return result;
	}

	protected Object mget(final List<String> keys) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				List<byte[]> listByteKeys = new ArrayList<byte[]>();
				for (String key : keys) {
					byte[] byteKey = serializer.serialize(key);
					listByteKeys.add(byteKey);
				}

				List<byte[]> listByteValues = connection.mGet(listByteKeys.toArray(new byte[listByteKeys.size()][]));
				List<Object> listValue = new ArrayList<Object>();
				for (byte[] value : listByteValues) {
					listValue.add(serializer.deserialize(value));
				}
				return listValue;
			}
		});
		return result;
	}

	protected <T extends Serializable> List<T> mget(final List<String> keys, final Class<T> clazz){
		List<T> result = redisTemplate.execute(new RedisCallback<List<T>>() {
			public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				List<byte[]> listByteKeys = new ArrayList<byte[]>();
				for (String key : keys) {
					byte[] byteKey = serializer.serialize(key);
					listByteKeys.add(byteKey);
				}

				List<byte[]> listByteValues = connection.mGet(listByteKeys.toArray(new byte[listByteKeys.size()][]));
				List<T> listValue = new ArrayList<T>();
				for (byte[] value : listByteValues) {
					listValue.add(CommonUtil.transByteArray2Obj(value, clazz));
				}
				return listValue;
			}
		});
		return result;
	}

	protected boolean exists(final String key) {
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				return connection.exists(byteKey);
			}
		});
		return result;
	}

	protected Long ttl(final String key) {
		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);

				return connection.ttl(byteKey);
			}
		});
		return result;
	}

	/////////////////////////////////////////////////////////////////
	/////////////////////////// Set 操作接口 //////////////////////////
	/////////////////////////////////////////////////////////////////
//	protected boolean sadd(final String key, final String value) {
//		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
//			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
//				RedisSerializer<String> serializer = getRedisSerializer();
//				byte[] byteKey = serializer.serialize(key);
//				byte[] byteValue = serializer.serialize(value);
//				connection.sAdd(byteKey, byteValue);
//				return true;
//			}
//		});
//		return result;
//	}

	/**
	 * 仅限于基本对象使用，Bean对象禁止使用
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	protected boolean sadd(final String key, final Serializable value) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteValue = CommonUtil.transObj2ByteArray(value);
				connection.sAdd(byteKey, byteValue);
				return true;
			}
		});
		return result;
	}

	protected boolean sadd(final String key, final List<String> values) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				List<byte[]> byteValues = new ArrayList<byte[]>(values.size());
				for (String v : values) {
					byteValues.add(serializer.serialize(v));
				}
				connection.sAdd(byteKey, byteValues.toArray(new byte[values.size()][]));

				return true;
			}
		});
		return result;
	}
	
	protected boolean sadd(final String key, final Set<Serializable> values) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[][] byteValues = new byte[values.size()][];
				int i = 0;
				for (Serializable v : values) {
					byteValues[i++] = CommonUtil.transObj2ByteArray(v);
				}
				connection.sAdd(byteKey, byteValues);
				return true;
			}
		});
		return result;
	}

	protected Long scard(final String key) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				Long count = connection.sCard(byteKey);
				return count;
			}
		});
		return Long.parseLong(String.valueOf(result));
	}

	@SuppressWarnings("rawtypes")
	protected Object smembers(final String key) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);

				Set setValue = connection.sMembers(byteKey);

				Iterator iter = setValue.iterator();

				List<Object> listObj = new ArrayList<Object>();
				while (iter.hasNext()) {
					Object obj = serializer.deserialize((byte[]) iter.next());
					listObj.add(obj);
				}
				return listObj;
			}
		});
		return result;
	}
	
	protected <T extends Serializable> Set<T> smembers(final String key, final Class<T> clazz) {
		Set<T> result = redisTemplate.execute(new RedisCallback<Set<T>>() {
			public Set<T> doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				Set<byte[]> setValue = connection.sMembers(byteKey);
				Iterator<byte[]> iter = setValue.iterator();
				Set<T> result = new HashSet<T>();
				while (iter.hasNext()) {
					T tmp = CommonUtil.transByteArray2Obj(iter.next(), clazz);
					result.add(tmp);
				}
				if (result.size() == 0) {
					return null;
				}
				return result;
			}
		});
		return result;
	}
	
	
	protected Set<String> smembersString(final String key) {
		Set<String> result = redisTemplate.execute(new RedisCallback<Set<String>>() {
			public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				Set<byte[]> setValue = connection.sMembers(byteKey);
				Iterator<byte[]> iter = setValue.iterator();
				Set<String> setStr = new HashSet<String>();
				while (iter.hasNext()) {
					String obj = serializer.deserialize(iter.next());
					setStr.add(obj);
				}
				return setStr;
			}
		});
		return result;
	}

	

	/**
	 * 移出set中指定元素，在事务中无法返回结果，若业务需要判断移出是否成功，请使用smember判断
	 * @param key
	 * @param value
	 * @return
	 */
//	protected boolean srem(final String key, final String value) {
//		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
//			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
//				RedisSerializer<String> serializer = getRedisSerializer();
//				byte[] byteKey = serializer.serialize(key);
//				byte[] byteValue = serializer.serialize(value);
//				Long index = connection.sRem(byteKey, byteValue);
//				return index == null? false : index > 0L;
//			}
//		});
//		return result;
//	}
	
	protected boolean srem(final String key, final Serializable value) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteValue = CommonUtil.transObj2ByteArray(value);
				Long index = connection.sRem(byteKey, byteValue);
				return index == null? false : index > 0L;
			}
		});
		return result;

	}
	
	protected boolean srem(final String key, final List<String> values) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[][] byteValues = new byte[values.size()][];
				for (int i = 0; i < values.size(); i++) {
					byteValues[i] = serializer.serialize(values.get(i));
				}
				Long index = connection.sRem(byteKey, byteValues);
				return true;
			}
		});
		return result;

	}

	/////////////////////////////////////////////////////////////////
	/////////////////////////// ZSet接口//////////////////////////////
	/////////////////////////////////////////////////////////////////
	protected boolean zadd(final String key, final Double score, final String value) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteValue = serializer.serialize(value);
				connection.zAdd(byteKey, score, byteValue);
				return true;
			}
		});
		return result;
	}

	protected boolean zrem(final String key, final String value) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteValue = serializer.serialize(value);
				connection.zRem(byteKey, byteValue);
				return true;
			}
		});
		return result;
	}

	protected Object zrange(final String key, final Long begin, final Long end) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);

				Set<byte[]> setValue = connection.zRange(byteKey, begin, end);
				Iterator<byte[]> iter = setValue.iterator();
				List<Object> listObj = new ArrayList<Object>();
				while (iter.hasNext()) {
					Object obj = serializer.deserialize((byte[]) iter.next());
					listObj.add(obj);
				}
				return listObj;
			}
		});
		return result;
	}

	@SuppressWarnings("rawtypes")
	protected Object zrangebyscore(final String key, final Double begin, final Double end) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);

				Set setValue = connection.zRangeByScore(byteKey, begin, end);
				Iterator iter = setValue.iterator();
				List<Object> listObj = new ArrayList<Object>();
				while (iter.hasNext()) {
					Object obj = serializer.deserialize((byte[]) iter.next());
					listObj.add(obj);
				}
				return listObj;
			}
		});
		return result;
	}

	/////////////////////////////////////////////////////////////////
	/////////////////////////// HSet接口//////////////////////////////
	/////////////////////////////////////////////////////////////////
	protected boolean hset(final String key, final String field, final String value, final Long seconds) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteField = serializer.serialize(field);
				byte[] byteValue = serializer.serialize(value);
				connection.hSet(byteKey, byteField, byteValue);
				if(seconds.equals(0l)){
					connection.persist(byteKey);
				}else{
					connection.expire(byteKey, seconds);// 设置过期时间
				}
				return true;
			}
		});
		return result;
	}

	protected boolean hset(final String key, final String field, final Serializable value, final Long seconds) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteField = serializer.serialize(field);
				byte[] byteValue = CommonUtil.transObj2ByteArray(value);
				connection.hSet(byteKey, byteField, byteValue);
				if(seconds.equals(0L)){
					connection.persist(byteKey);
				}else{
					connection.expire(byteKey, seconds);// 设置过期时间	
				}
				return true;
			}
		});
		return result;
	}
	
	protected Object hget(final String key, final String field) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteField = serializer.serialize(field);
				byte[] value = connection.hGet(byteKey, byteField);
				if (value == null) {
					return null;
				}
				Object dataObj = serializer.deserialize(value);
				return dataObj;
			}
		});
		return result;
	}

	protected <T extends Serializable> T hget(final String key, final String field, final Class<T> clazz) {
		T result = redisTemplate.execute(new RedisCallback<T>() {
			public T doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteField = serializer.serialize(field);
				byte[] value = connection.hGet(byteKey, byteField);
				if (value == null) {
					return null;
				}
				T dataObj = CommonUtil.transByteArray2Obj(value, clazz);
				return dataObj;
			}
		});
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	protected Object hkeys(final String key) {
		Object result = redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);

				Set setValue = connection.hKeys(byteKey);

				Iterator iter = setValue.iterator();

				List<Object> listObj = new ArrayList<Object>();
				while (iter.hasNext()) {
					Object obj = serializer.deserialize((byte[]) iter.next());
					listObj.add(obj);
				}
				return listObj;
			}
		});
		return result;
	}

	protected Set<String> hkeysSerial(final String key) {
		Set<String> result = redisTemplate.execute(new RedisCallback<Set<String>>() {
			public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);

				Set<byte[]> setValue = connection.hKeys(byteKey);

				Iterator<byte[]> iter = setValue.iterator();

				Set<String> keys = new HashSet<String>();
				while (iter.hasNext()) {
					String obj = serializer.deserialize((byte[]) iter.next());
					keys.add(obj);
				}
				return keys;
			}
		});
		return result;
	}
	
	protected boolean hdel(final String key, final String field) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteField = serializer.serialize(field);
				connection.hDel(byteKey, byteField);
				return true;
			}
		});
		return result;
	}
	
	/**
	 * 设置redis键过期时间
	 * 
	 * @param key
	 * @param seconds
	 */
	protected boolean expire(final String key, final Long seconds) {
		boolean result = transRedisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				connection.expire(byteKey, seconds);// 设置过期时间
				return true;
			}
		});
		return result ;
	}

	/**
	 * @param keys
	 */
	public void delete(List<K> keys) {
		transRedisTemplate.delete(keys);
	}

	public Set<K> keys(K pattern) {
		return redisTemplate.keys(pattern);
	}

	public void clean(K pattern) {
		Set<K> keySet = transRedisTemplate.keys(pattern);
		if (keySet != null && keySet.size() > 0) {
			List<K> keyList = new ArrayList<K>(keySet);
			delete(keyList);
		}
	}

	/**
	 * pipeline方式获取数据
	 * 
	 * @param keys
	 * @return
	 */
	protected List<Object> pipelineGet(final List<String> keys) {
		return redisTemplate.executePipelined(new RedisCallback<Object>() {

			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				// 直接执行所有需要执行的命令，返回值由jedis通过底层connection.closePipeline()实现
				RedisSerializer<String> serializer = getRedisSerializer();
				for (String key : keys) {
					byte[] byteKey = serializer.serialize(key);
					connection.get(byteKey);
				}
				// jedis api限制 只能返回null 否则会抛异常
				return null;
			}

		}, getRedisSerializer());

	}

	protected <T extends Serializable> List<T> pipelineGet(final Collection<String> keys, final Class<T> clazz) {
		List<?> tmps = redisTemplate.executePipelined(new RedisCallback<Object>() {

			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				// 直接执行所有需要执行的命令，返回值由jedis通过底层connection.closePipeline()实现
				RedisSerializer<String> serializer = getRedisSerializer();
				for (String key : keys) {
					byte[] byteKey = serializer.serialize(key);
					connection.get(byteKey);
				}
				// jedis api限制 只能返回null 否则会抛异常
				return null;
			}

		}, getPojoSerializer(clazz));
		List<T> result = new ArrayList<T>(tmps.size());
		for (Object obj : tmps) {
			result.add(clazz.cast(obj));
		}
		return result;
	}

	/////////////////////////////////////////////////////////////
	/////////////////////// Pipeline 方式 /////////////////////////
	/////////////////////////////////////////////////////////////
	// 用pipeline的方式实现get多个数据
	protected List<String> pget(final List<String> keys) {

		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) redisTemplate.execute(new RedisCallback<Object>() {
			public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
				connection.openPipeline();

				RedisSerializer<String> serializer = getRedisSerializer();
				for (String key : keys) {
					byte[] byteKey = serializer.serialize(key);
					connection.get(byteKey);
				}
				List<?> pipeResult = connection.closePipeline();
				List<String> strObjs = new ArrayList<String>();
				for (Object tmp : pipeResult) {
					byte[] bytes = (byte[]) tmp;
					String tmpObj = serializer.deserialize(bytes);
					strObjs.add(tmpObj);
				}
				return strObjs;
			}
		});

		return result;
	}
	
	/**
	 * 判断 value 元素是否集合(set)  key 的成员
	 * @param key
	 * @param value
	 * @return
	 */
	protected boolean sIsMember(final String key,final String value) {
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] byteKey = serializer.serialize(key);
				byte[] byteVal = serializer.serialize(value);
				return connection.sIsMember(byteKey, byteVal);
			}
		});
		return result;
	}


	/**
	 * 设置RedisTemplate 可以注入，不需要显示调用
	 * 
	 * @return
	 */
	protected RedisTemplate<K, V> getRedisTemplate() {
		return transRedisTemplate;
	}

	/**
	 * 
	 * @param redisTemplate
	 */
	public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
		this.transRedisTemplate = redisTemplate;
	}

	/**
	 * 
	 * @return
	 */
	protected RedisSerializer<String> getRedisSerializer() {

		return transRedisTemplate.getStringSerializer();
	}

	protected <T extends Serializable> RedisSerializer<T> getPojoSerializer(Class<T> clazz) {
		return new PojoSerializable<T>(clazz);
	}

	public void closeConn() {
		RedisConnection conn = RedisConnectionUtils.getConnection(transRedisTemplate.getConnectionFactory());
		conn.close();
	}

	
}
