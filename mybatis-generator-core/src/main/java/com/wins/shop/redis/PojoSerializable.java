package com.wins.shop.redis;

import java.io.Serializable;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.wins.shop.util.CommonUtil;

public class PojoSerializable<T extends Serializable> implements RedisSerializer<T> {

	private Class<T> clazz;

	public PojoSerializable(Class<T> clazz) {
		this.clazz = clazz;
	}

	public byte[] serialize(T t) throws SerializationException {
		return CommonUtil.transObj2ByteArray(t);
	}

	public T deserialize(byte[] bytes) throws SerializationException {
		return CommonUtil.transByteArray2Obj(bytes, clazz);
	}

}
