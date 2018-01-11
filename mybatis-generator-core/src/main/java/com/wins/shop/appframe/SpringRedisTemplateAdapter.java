package com.wins.shop.appframe;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.BulkMapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.script.ScriptExecutor;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class SpringRedisTemplateAdapter<K, V> extends RedisTemplate<K, V> {
	private ThreadLocal<RedisTemplate<K, V>> localTemplate = new ThreadLocal<RedisTemplate<K, V>>();
	private RedisConnectionFactory connectionFactory;
	private RedisSerializer<?> defaultSerailizer = new JdkSerializationRedisSerializer();
	private boolean enableDefaultSerializer = false;
	private boolean enableTransactionSupport = false;
	private boolean exposeConnection = false;
	private RedisSerializer<?> hashKeySerializer;
	private RedisSerializer<?> hashValueSerializer;
	private RedisSerializer<?> keySerializer;
	private ScriptExecutor<K> scriptExecutor;
	private RedisSerializer<String> stringSerializer = new StringRedisSerializer();
	private RedisSerializer<?> valueSerializer;

	public SpringRedisTemplateAdapter() {
	}

	public void afterPropertiesSet() {
		return;
	}

	public <T> T execute(RedisCallback<T> action) {
		return getTemplate().execute(action);
	}

	/**
	 * Executes the given action object within a connection, which can be
	 * exposed or not.
	 * 
	 * @param <T>
	 *            return type
	 * @param action
	 *            callback object that specifies the Redis action
	 * @param exposeConnection
	 *            whether to enforce exposure of the native Redis Connection to
	 *            callback code
	 * @return object returned by the action
	 */
	public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
		return getTemplate().execute(action, exposeConnection);
	}

	/**
	 * Executes the given action object within a connection that can be exposed
	 * or not. Additionally, the connection can be pipelined. Note the results
	 * of the pipeline are discarded (making it suitable for write-only
	 * scenarios).
	 * 
	 * @param <T>
	 *            return type
	 * @param action
	 *            callback object to execute
	 * @param exposeConnection
	 *            whether to enforce exposure of the native Redis Connection to
	 *            callback code
	 * @param pipeline
	 *            whether to pipeline or not the connection for the execution
	 * @return object returned by the action
	 */
	public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
		return getTemplate().execute(action, exposeConnection, pipeline);
	}

	public <T> T execute(SessionCallback<T> session) {
		return getTemplate().execute(session);
	}

	public List<Object> executePipelined(final SessionCallback<?> session) {
		return getTemplate().executePipelined(session);
	}

	public List<Object> executePipelined(final SessionCallback<?> session, final RedisSerializer<?> resultSerializer) {
		return getTemplate().executePipelined(session, resultSerializer);
	}

	public List<Object> executePipelined(final RedisCallback<?> action) {
		return getTemplate().executePipelined(action);
	}

	public List<Object> executePipelined(final RedisCallback<?> action, final RedisSerializer<?> resultSerializer) {
		return getTemplate().executePipelined(action, resultSerializer);
	}

	public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
		return getTemplate().execute(script, keys, args);
	}

	public <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer, RedisSerializer<T> resultSerializer,
			List<K> keys, Object... args) {
		return getTemplate().execute(script, argsSerializer, resultSerializer, keys, args);
	}

	/**
	 * Returns whether to expose the native Redis connection to RedisCallback
	 * code, or rather a connection proxy (the default).
	 * 
	 * @return whether to expose the native Redis connection or not
	 */
	public boolean isExposeConnection() {
		return getTemplate().isExposeConnection();
	}

	/**
	 * Sets whether to expose the Redis connection to {@link RedisCallback}
	 * code. Default is "false": a proxy will be returned, suppressing
	 * <tt>quit</tt> and <tt>disconnect</tt> calls.
	 * 
	 * @param exposeConnection
	 */
	public void setExposeConnection(boolean exposeConnection) {
		this.exposeConnection = exposeConnection;
	}

	/**
	 * @return Whether or not the default serializer should be used. If not, any
	 *         serializers not explicilty set will remain null and values will
	 *         not be serialized or deserialized.
	 */
	public boolean isEnableDefaultSerializer() {
		return getTemplate().isEnableDefaultSerializer();
	}

	/**
	 * @param enableDefaultSerializer
	 *            Whether or not the default serializer should be used. If not,
	 *            any serializers not explicilty set will remain null and values
	 *            will not be serialized or deserialized.
	 */
	public void setEnableDefaultSerializer(boolean enableDefaultSerializer) {
		this.enableDefaultSerializer = enableDefaultSerializer;
	}

	/**
	 * Returns the default serializer used by this template.
	 * 
	 * @return template default serializer
	 */
	public RedisSerializer<?> getDefaultSerializer() {
		return getTemplate().getDefaultSerializer();
	}

	/**
	 * Sets the default serializer to use for this template. All serializers
	 * (expect the {@link #setStringSerializer(RedisSerializer)}) are
	 * initialized to this value unless explicitly set. Defaults to
	 * {@link JdkSerializationRedisSerializer}.
	 * 
	 * @param serializer
	 *            default serializer to use
	 */
	public void setDefaultSerializer(RedisSerializer<?> serializer) {
		this.defaultSerailizer = serializer;
	}

	/**
	 * Sets the key serializer to be used by this template. Defaults to
	 * {@link #getDefaultSerializer()}.
	 * 
	 * @param serializer
	 *            the key serializer to be used by this template.
	 */
	public void setKeySerializer(RedisSerializer<?> serializer) {
		this.keySerializer = serializer;
	}

	/**
	 * Returns the key serializer used by this template.
	 * 
	 * @return the key serializer used by this template.
	 */
	public RedisSerializer<?> getKeySerializer() {
		return getTemplate().getKeySerializer();
	}

	/**
	 * Sets the value serializer to be used by this template. Defaults to
	 * {@link #getDefaultSerializer()}.
	 * 
	 * @param serializer
	 *            the value serializer to be used by this template.
	 */
	public void setValueSerializer(RedisSerializer<?> serializer) {
		this.valueSerializer = serializer;
	}

	/**
	 * Returns the value serializer used by this template.
	 * 
	 * @return the value serializer used by this template.
	 */
	public RedisSerializer<?> getValueSerializer() {
		return getTemplate().getValueSerializer();
	}

	/**
	 * Returns the hashKeySerializer.
	 * 
	 * @return Returns the hashKeySerializer
	 */
	public RedisSerializer<?> getHashKeySerializer() {
		return getTemplate().getHashKeySerializer();
	}

	/**
	 * Sets the hash key (or field) serializer to be used by this template.
	 * Defaults to {@link #getDefaultSerializer()}.
	 * 
	 * @param hashKeySerializer
	 *            The hashKeySerializer to set.
	 */
	public void setHashKeySerializer(RedisSerializer<?> hashKeySerializer) {
		this.hashKeySerializer = hashKeySerializer;
	}

	/**
	 * Returns the hashValueSerializer.
	 * 
	 * @return Returns the hashValueSerializer
	 */
	public RedisSerializer<?> getHashValueSerializer() {
		return getTemplate().getHashValueSerializer();
	}

	/**
	 * Sets the hash value serializer to be used by this template. Defaults to
	 * {@link #getDefaultSerializer()}.
	 * 
	 * @param hashValueSerializer
	 *            The hashValueSerializer to set.
	 */
	public void setHashValueSerializer(RedisSerializer<?> hashValueSerializer) {
		this.hashValueSerializer = hashValueSerializer;
	}

	/**
	 * Returns the stringSerializer.
	 * 
	 * @return Returns the stringSerializer
	 */
	public RedisSerializer<String> getStringSerializer() {
		return getTemplate().getStringSerializer();
	}

	/**
	 * Sets the string value serializer to be used by this template (when the
	 * arguments or return types are always strings). Defaults to
	 * {@link StringRedisSerializer}.
	 * 
	 * @see ValueOperations#get(Object, long, long)
	 * @param stringSerializer
	 *            The stringValueSerializer to set.
	 */
	public void setStringSerializer(RedisSerializer<String> stringSerializer) {
		this.stringSerializer = stringSerializer;
	}

	/**
	 * @param scriptExecutor
	 *            The {@link ScriptExecutor} to use for executing Redis scripts
	 */
	public void setScriptExecutor(ScriptExecutor<K> scriptExecutor) {
		this.scriptExecutor = scriptExecutor;
	}

	//
	// RedisOperations
	//

	/**
	 * Execute a transaction, using the default {@link RedisSerializer}s to
	 * deserialize any results that are byte[]s or Collections or Maps of
	 * byte[]s or Tuples. Other result types (Long, Boolean, etc) are left as-is
	 * in the converted results. If conversion of tx results has been disabled
	 * in the {@link RedisConnectionFactory}, the results of exec will be
	 * returned without deserialization. This check is mostly for backwards
	 * compatibility with 1.0.
	 * 
	 * @return The (possibly deserialized) results of transaction exec
	 */
	public List<Object> exec() {
		return getTemplate().exec();
	}

	public List<Object> exec(RedisSerializer<?> valueSerializer) {
		return getTemplate().exec(valueSerializer);
	}

	public void delete(K key) {
		getTemplate().delete(key);
	}

	public void delete(Collection<K> keys) {
		getTemplate().delete(keys);
	}

	public Boolean hasKey(K key) {
		return getTemplate().hasKey(key);
	}

	public Boolean expire(K key, final long timeout, final TimeUnit unit) {
		return getTemplate().expire(key, timeout, unit);
	}

	public Boolean expireAt(K key, final Date date) {
		return getTemplate().expireAt(key, date);
	}

	public void convertAndSend(String channel, Object message) {
		getTemplate().convertAndSend(channel, message);
	}

	//
	// Value operations
	//

	public Long getExpire(K key) {
		return getTemplate().getExpire(key);
	}

	public Long getExpire(K key, final TimeUnit timeUnit) {
		return getTemplate().getExpire(key, timeUnit);
	}

	public Set<K> keys(K pattern) {
		return getTemplate().keys(pattern);
	}

	public Boolean persist(K key) {
		return getTemplate().persist(key);
	}

	public Boolean move(K key, final int dbIndex) {
		return getTemplate().move(key, dbIndex);
	}

	public K randomKey() {
		return getTemplate().randomKey();
	}

	public void rename(K oldKey, K newKey) {
		getTemplate().rename(oldKey, newKey);
	}

	public Boolean renameIfAbsent(K oldKey, K newKey) {
		return getTemplate().renameIfAbsent(oldKey, newKey);
	}

	public DataType type(K key) {
		return getTemplate().type(key);
	}

	/**
	 * Executes the Redis dump command and returns the results. Redis uses a
	 * non-standard serialization mechanism and includes checksum information,
	 * thus the raw bytes are returned as opposed to deserializing with
	 * valueSerializer. Use the return value of dump as the value argument to
	 * restore
	 * 
	 * @param key
	 *            The key to dump
	 * @return results The results of the dump operation
	 */
	public byte[] dump(K key) {
		return getTemplate().dump(key);
	}

	/**
	 * Executes the Redis restore command. The value passed in should be the
	 * exact serialized data returned from {@link #dump(Object)}, since Redis
	 * uses a non-standard serialization mechanism.
	 * 
	 * @param key
	 *            The key to restore
	 * @param value
	 *            The value to restore, as returned by {@link #dump(Object)}
	 * @param timeToLive
	 *            An expiration for the restored key, or 0 for no expiration
	 * @param unit
	 *            The time unit for timeToLive
	 * @throws RedisSystemException
	 *             if the key you are attempting to restore already exists.
	 */
	public void restore(K key, final byte[] value, long timeToLive, TimeUnit unit) {
		getTemplate().restore(key, value, timeToLive, unit);
		;
	}

	public void multi() {
		getTemplate().multi();
	}

	public void discard() {
		getTemplate().discard();
	}

	public void watch(K key) {
		getTemplate().watch(key);
	}

	public void watch(Collection<K> keys) {
		getTemplate().watch(keys);
	}

	public void unwatch() {
		getTemplate().unwatch();
	}

	// Sort operations

	public List<V> sort(SortQuery<K> query) {
		return getTemplate().sort(query);
	}

	public <T> List<T> sort(SortQuery<K> query, RedisSerializer<T> resultSerializer) {
		return getTemplate().sort(query, resultSerializer);
	}

	public <T> List<T> sort(SortQuery<K> query, BulkMapper<T, V> bulkMapper) {
		return getTemplate().sort(query, bulkMapper);
	}

	public <T, S> List<T> sort(SortQuery<K> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer) {
		return getTemplate().sort(query, bulkMapper, resultSerializer);
	}

	public Long sort(SortQuery<K> query, K storeKey) {
		return getTemplate().sort(query, storeKey);
	}

	public BoundValueOperations<K, V> boundValueOps(K key) {
		return getTemplate().boundValueOps(key);
	}

	public ValueOperations<K, V> opsForValue() {
		return getTemplate().opsForValue();
	}

	public ListOperations<K, V> opsForList() {
		return getTemplate().opsForList();
	}

	public BoundListOperations<K, V> boundListOps(K key) {
		return getTemplate().boundListOps(key);
	}

	public BoundSetOperations<K, V> boundSetOps(K key) {
		return getTemplate().boundSetOps(key);
	}

	public SetOperations<K, V> opsForSet() {
		return getTemplate().opsForSet();
	}

	public BoundZSetOperations<K, V> boundZSetOps(K key) {
		return getTemplate().boundZSetOps(key);
	}

	public ZSetOperations<K, V> opsForZSet() {
		return getTemplate().opsForZSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.core.RedisOperations#opsForHyperLogLog()
	 */
	public HyperLogLogOperations<K, V> opsForHyperLogLog() {
		return getTemplate().opsForHyperLogLog();
	}

	public <HK, HV> BoundHashOperations<K, HK, HV> boundHashOps(K key) {
		return getTemplate().boundHashOps(key);
	}

	public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
		return getTemplate().opsForHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.core.RedisOperations#killClient(java.lang.
	 * Object)
	 */
	public void killClient(final String host, final int port) {
		getTemplate().killClient(host, port);
	}

	public List<RedisClientInfo> getClientList() {
		return getTemplate().getClientList();
	}

	/*
	 * @see
	 * org.springframework.data.redis.core.RedisOperations#slaveOf(java.lang.
	 * String, int)
	 */
	public void slaveOf(final String host, final int port) {
		getTemplate().slaveOf(host, port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.RedisOperations#slaveOfNoOne()
	 */
	public void slaveOfNoOne() {
		getTemplate().slaveOfNoOne();
	}

	/**
	 * If set to {@code true} {@link RedisTemplate} will use
	 * {@literal MULTI...EXEC|DISCARD} to keep track of operations.
	 * 
	 * @param enableTransactionSupport
	 * @since 1.3
	 */
	public void setEnableTransactionSupport(boolean enableTransactionSupport) {
		getTemplate().setEnableTransactionSupport(enableTransactionSupport);
	}

	public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public RedisConnectionFactory getConnectionFactory() {
		return getTemplate().getConnectionFactory();
	}

	private RedisTemplate<K, V> getTemplate() {
		RedisTemplate<K, V> temp = localTemplate.get();
		if(temp == null){
			temp = new RedisTemplate<K, V>();
			temp.setConnectionFactory(this.connectionFactory);
			temp.setDefaultSerializer(this.defaultSerailizer);
			temp.setEnableDefaultSerializer(this.enableDefaultSerializer);
			temp.setEnableTransactionSupport(this.enableTransactionSupport);
			temp.setExposeConnection(this.exposeConnection);
			temp.setHashKeySerializer(this.hashKeySerializer);
			temp.setHashValueSerializer(this.hashValueSerializer);
			temp.setKeySerializer(this.keySerializer);
			temp.setScriptExecutor(this.scriptExecutor);
			temp.setStringSerializer(this.stringSerializer);
			temp.setValueSerializer(this.valueSerializer);
			temp.afterPropertiesSet();
			localTemplate.set(temp);
		}
		return temp;
	}
}
