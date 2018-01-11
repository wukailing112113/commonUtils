package com.wins.shop.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

public class CommonUtil {
	private static final Logger logger = Logger.getLogger(CommonUtil.class);

	/**
	 * 关闭输出流代码
	 * 
	 * @author shenxufei
	 * @param is
	 */
	public static void closeInpputStream(InputStream is) {
		try {
			is.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 关闭输出流代码
	 * 
	 * @author shenxufei
	 * @param os
	 */
	public static void closeOutputStream(OutputStream os) {
		try {
			os.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 对象转数组
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] transObj2ByteArray(Serializable input) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(input);
			return baos.toByteArray();
		} catch (IOException e) {
			logger.debug("序列化失败", e);
			return null;
		} finally {
			closeOutputStream(oos);
			closeOutputStream(baos);
		}
	}

	/**
	 * 数组转换成对象
	 * 
	 * @param input
	 * @param clazz
	 * @return
	 */
	public static <T extends Serializable> T transByteArray2Obj(byte[] input, Class<T> clazz) {
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(input);
			ois = new ObjectInputStream(bis);
			Object obj = ois.readObject();
			return clazz.cast(obj);
		} catch (Exception e) {
			logger.debug("反序列化失败", e);
			return null;
		} finally {
			closeInpputStream(bis);
			closeInpputStream(ois);
		}
	}

	/**
	 * 获取图片MIME类型
	 * 
	 * @param img
	 * @return
	 */
	public static MagicMatch getImageMIME(byte[] img) {
		try {
			MagicMatch match = Magic.getMagicMatch(img);
			return match;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getFileSuffix(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1 || index == (fileName.length() - 1)) {
			return null;
		}
		return fileName.substring(index + 1);
	}

	public static String getJsonProp(String jsonObj, String propName) {
		JSONObject obj = JSON.parseObject(jsonObj);
		return obj.getString(propName);
	}

	public static String gerStatusTime(String orgStatus, Byte status, String time) {
		// 获取员状态数组信息
		JSONArray orgStatusArray = null;
		if (orgStatus == null) {
			orgStatusArray = new JSONArray();
		} else {
			orgStatusArray = JSON.parseArray(orgStatus);
		}
		// 原状态信息格式不正确
		if (null == orgStatusArray) {
			return null;
		}
		// 需要增加的状态信息
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sts", status);
		jsonObj.put("time", time);
		orgStatusArray.add(jsonObj);
		return orgStatusArray.toJSONString();
	}

	// 根据订单状态或者订单修改时间
	public static Date getStatusTimeByStatus(Byte status, String orgStatus) {
		JSONArray orgStatusArray = null;
		if (orgStatus == null || status == null) {
			return null;
		} else {
			orgStatusArray = JSON.parseArray(orgStatus);
		}
		for (int i = 0; i < orgStatusArray.size(); i ++) {
			JSONObject obj = orgStatusArray.getJSONObject(i);
			if (status.equals(obj.getByte("sts"))) {
				return obj.getDate("time");
			}
		}
		return null;
	}

	
	
	
	/**
	 * 简单java对象转换成json字符串
	 * 
	 * @param pojo
	 * @return
	 */
	public static String gerObjJsonIncludeTransiant(Object pojo) {
		Object jObj = JSON.toJSON(pojo);
		if (!(jObj instanceof JSONObject)) {
			return null;
		}
		JSONObject jsonObj = (JSONObject) jObj;
		Field[] fields = pojo.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isTransient(field.getModifiers())) {
				String fieldName = field.getName();
				try {
					Object tObj = ReflectUtil.getObjField(pojo, fieldName);
					jsonObj.put(fieldName, JSON.toJSON(tObj));
				} catch (Exception e) {
					return null;
				}
			}
		}
		return jsonObj.toJSONString();
	}

	public static <T extends Serializable> T cloneObj(T obj, Class<T> clazz) {
		byte[] objByte = transObj2ByteArray(obj);
		T result = transByteArray2Obj(objByte, clazz);
		return result;
	}

	public static <T> Map<String, String> obj2Map(T t, Class<T> clazz) {
		Map<String, String> map = new HashMap<String, String>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Object obj = null;
			try {
				obj = field.get(t);
				if (null != obj && StringUtil.isNotEmptyString(obj.toString())) {
					map.put(field.getName(), obj.toString());
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * json数组转换为List
	 * 
	 * @param array
	 *            需要转换的json数组
	 * @param ts
	 *            array中的对象集合（数组长度固定为0）
	 * @return
	 */
	public static <T> List<T> jsonArr2List(JSONArray array, Class<T> tCls) {
		JSONObject[] tArr = array.toArray(new JSONObject[0]);
		List<T> result = new ArrayList<T>(tArr.length);
		for (JSONObject obj : tArr) {
			T tmp = JSON.toJavaObject(obj, tCls);
			result.add(tmp);
		}
		return result;
	}

	public static Float getFixFloat(Float input, int fixNumber) {
		if(input == null){
			return null;
		}
		BigDecimal b = new BigDecimal(input);
		Float f1 = b.setScale(fixNumber, BigDecimal.ROUND_HALF_UP).floatValue();
		return f1;
	}
	
	public static <T> String getAttrVal4ForRedisKey(T t) {
		StringBuffer sb = new StringBuffer();
		Field fields[] = t.getClass().getDeclaredFields();
		Field field = null;

		try {
			for (int i = 0; i < fields.length; i++) {
				field = fields[i];
				field.setAccessible(true);
				Object val = field.get(t);
				if(val != null){
					sb.append(field.getName()).append("-").append(val);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		logger.debug("========" + t.getClass().getSimpleName() + ":" + sb.toString());
		String val = SecureUtil.md5X16Str(sb.toString(), "utf-8") ;
		sb = new StringBuffer(t.getClass().getSimpleName()).append(":").append(val) ;
		return sb.toString();
	}
	
}
