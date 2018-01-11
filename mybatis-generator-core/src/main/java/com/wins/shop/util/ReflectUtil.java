package com.wins.shop.util;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;


public class ReflectUtil {
	private static final Logger logger = Logger.getLogger(ReflectUtil.class);

	public static Object getObjField(Object pojo, String fieldName) throws Exception {
		if (StringUtil.isEmptyString(fieldName)) {
			throw new Exception("errorField");
		}
		StringBuilder getName = new StringBuilder("get");
		getName.append(fieldName.substring(0, 1).toUpperCase());
		getName.append(fieldName.substring(1));
		Method getMethod = pojo.getClass().getMethod(getName.toString());
		return getMethod.invoke(pojo);
	}

	public static <T> void setObjField(Object pojo, String fieldName, Class<T> valType, T val) throws Exception {
		if(StringUtil.isEmptyString(fieldName)){
			throw new Exception("errorField");
		}
		StringBuilder setName = new StringBuilder("set");
		setName.append(fieldName.substring(0, 1).toUpperCase());
		setName.append(fieldName.substring(1));
		Method setMethod = pojo.getClass().getMethod(setName.toString(), valType);
		setMethod.invoke(pojo, val);
	}

	/**
	 * bean属性值拷贝，null不处理
	 * @param src 源bean
	 * @param obj 目标备案
	 * @throws Exception 
	 */
	public static <T> void copyProp(T src, T obj){
		if(src.getClass() != obj.getClass()){
			return;
		}
		try {
			Class<?> clazz = src.getClass();
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if(!methodName.startsWith("get")){
					continue;
				}
				Object srcBean = method.invoke(src);
				if(null == srcBean){
					continue;
				}
				String beanName = methodName.substring(3);
				Class<?> paramType = method.getReturnType();
				Method setMethod = clazz.getMethod("set"+beanName, paramType);
				setMethod.invoke(obj, srcBean);
			}
		} catch (Exception e) {
			logger.debug("复制对象属性失败", e);
			return;
		}
	}
}
