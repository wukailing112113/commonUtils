package com.wins.shop.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;

public class StringUtil {

	public static Boolean isEmptyJson(final String jsonValue) {
		return (null == jsonValue || "" == jsonValue || "[]" == jsonValue);
	}

	public static Boolean isEmptyString(final String value) {
		return (null == value || "".equals(value.trim()));
	}

	public static Boolean isNotEmptyString(final String value) {
		return !isEmptyString(value);
	}

	/**
	 * 合并字符串集合
	 * 
	 * @author shenxufei
	 * @param inputs
	 *            字符串集合
	 * @return
	 */
	public static String combineString(String... inputs) {
		StringBuilder tmp = new StringBuilder();
		for (String string : inputs) {
			tmp.append(string);
		}
		return tmp.toString();
	}

	/**
	 * 生成指定长度字符串，不足部分补齐指定字符串
	 * 
	 * @author shenxufei
	 * @param val
	 *            原始字符串
	 * @param pad
	 *            补齐不足部分使用字符串
	 * @param length
	 *            最终要生成的字符串长度
	 * @return
	 */
	public static String lpad(String val, String pad, Integer length) {
		if (null != val && val.length() >= length) {
			return val;
		}
		// 生成追加部分字符串
		StringBuilder sb = new StringBuilder(pad);
		int orgLength = null == val ? 0 : val.length();
		int padLength = length - orgLength;
		while (sb.length() < padLength) {
			sb.append(pad);
		}
		// 截取多余部分
		StringBuilder result = new StringBuilder(sb.substring(sb.length() - padLength));
		if (null != val) {
			result.append(val);
		}
		return result.toString();
	}

	/**
	 * 右补全
	 * 
	 * @param val
	 * @param pad
	 * @param length
	 * @return
	 */
	public static String rpad(String val, String pad, Integer length) {
		if (null != val && val.length() >= length) {
			return val;
		}
		// 生成追加部分字符串
		StringBuilder sb = new StringBuilder(val);
		while (sb.length() < length) {
			sb.append(pad);
		}
		return sb.toString().substring(0, length);
	}

	public static String getNoBlankStr(String input) {
		if (isEmptyString(input)) {
			return null;
		}
		return input;
	}

	public static String getNoNullStr(String input) {
		return (input == null ? "" : input.trim());
	}

	public static String getMatch(String input, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input);
		if (m.find()) {
			return m.group();
		}
		return null;
	}

	public static String captureMatch(String input, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	/**
	 * url字符串增加参数
	 * 
	 * @param url
	 * @param paramName
	 * @param paramVal
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String addParam2Url(String url, String paramName, String paramVal, String encode)
			throws UnsupportedEncodingException {
		StringBuilder tmp = new StringBuilder(url);
		if (url.contains("?") || url.contains("&")) {
			tmp.append("&");
		} else {
			tmp.append("?");
		}
		tmp.append(paramName).append("=").append(URLEncoder.encode(paramVal, encode));
		return tmp.toString();
	}

	/**
	 * url字符串增加参数
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String addParams2Url(String url, Map<String, String> params, String encode)
			throws UnsupportedEncodingException {
		Iterator<String> keyIter = params.keySet().iterator();
		StringBuilder tmp = new StringBuilder(url);
		if (keyIter.hasNext()) {
			if (url.contains("?") || url.contains("&")) {
				tmp.append("&");
			} else {
				tmp.append("?");
			}
			String key = keyIter.next();
			tmp.append(key).append("=").append(URLEncoder.encode(params.get(key), encode));
		}
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			tmp.append("&").append(key).append("=").append(URLEncoder.encode(params.get(key), encode));
		}
		return tmp.toString();
	}

	public static String addParams2UrlSorted(String url, Map<String, String> params, String encode)
			throws UnsupportedEncodingException {
		List<String> listkey = new ArrayList<String>(params.keySet());
		StringBuilder tmp = new StringBuilder(url);
		Collections.sort(listkey);
		String key = null;
		String val = null;
		for (int i = 0; i < listkey.size(); i++) {
			key = listkey.get(i);
			val = params.get(key);
			if (i == 0) {
				if (url.contains("?") || url.contains("&")) {
				} else {
					tmp.append("?");
				}
			} else {
				tmp.append("&");
			}
			tmp.append(key).append("=").append(URLEncoder.encode(val, encode));
		}
		return tmp.toString();
	}

	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys,String.CASE_INSENSITIVE_ORDER);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (isNotEmptyString(value)){
				if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
					prestr = prestr + key + "=" + value;
				} else {
					prestr = prestr + key + "=" + value + "&";
				}
			}
		}
		return prestr;
	}

	/**
	 * @param bytes
	 * @return
	 */
	public static byte[] decodeBase64(final String input) {
		return Base64.decodeBase64(input);
	}

	/**
	 * 二进制数据编码为BASE64字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64(final byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}

	/**
	 * 功能：前台交易构造HTTP POST自动提交表单<br>
	 * 
	 * @param reqUrl
	 *            表单提交地址<br>
	 * @param hiddens
	 *            以MAP形式存储的表单键值<br>
	 * @param method
	 *            POST或者GET提交<br>
	 * @return 构造好的HTTP POST或者GET交易表单<br>
	 */
	public static String createAutoFormHtml(String reqUrl, Map<String, String> hiddens, String method,String acceptCharset,String onSubmit) {
		StringBuffer sf = new StringBuffer();
		String otherParams = (isNotEmptyString(acceptCharset) ? "  accept-charset=\"" + acceptCharset + "\"":"")
				+(isNotEmptyString(onSubmit) ? "  onsubmit=\"" + onSubmit + "\"":"");
		sf.append("<form id = \"pay_form\" action=\"" + reqUrl + "\" method=\"" + method +  "\"" + otherParams+">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				if (isNotEmptyString(value)){
					sf.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + value + "\"/>");
				}
				
			}
		}
		sf.append("</form>");
		return sf.toString();
	}

	public static String getRandomStringByLength(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * isValidBankCardNo: 是否是正确的银行卡卡号， TODO:目前只是使用正则判断，以后改成用Luhn算法判断 
	 *
	 * @param cardNo   银联卡号
	 * @param bankId   银行机构id
	 * @param acctChildType  账户子类型：1-储蓄卡；2-信用卡
	 * @return
	 */
	public static boolean isValidBankCardNo(String cardNo, Short bankId, Byte acctChildType) {
		if (cardNo == null || "".equals(cardNo.trim())) {
			return false;
		}
		
		Pattern p = Pattern.compile("^[0-9]+");
		Matcher m = p.matcher(cardNo);
		if (!m.matches()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * parseNumber: 将文字转成Number， 不抛出错误 <br/> 
	 * 此方法和apache-commons里面的NumberUtils的toXX方法类似
	 * @param numberString  字符串
	 * @param numberClass   支持Integer.class Long.class Short.class
	 * @param defaultValue  如果转换失败, 返回的默认值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T parseNumber(String numberString, Class<T> numberClass, T defaultValue) {
		try {
	        if(numberClass.equals(Integer.class)) {
	            return (T) Integer.valueOf(numberString);
	        }
	        else if(numberClass.equals(Long.class)) {
	            return (T) Long.valueOf(numberString);
	        }
	        else if(numberClass.equals(Short.class)) {
	            return (T) Short.valueOf(numberString);
	        }
		} catch (NumberFormatException ex) {
			return defaultValue;
		} catch (NullPointerException ex ) {
	        return defaultValue;
	    } catch (Exception ex) {
	    	return defaultValue;
	    }
		return null;
	}
	
	/**
	 * parseIntegerDfZero: 将文字转成Integer， 不抛出错误 <br/>  
	 * @param intString 		字符串
	 * @param defaultValue   如果转换失败, 返回的默认值
	 * @return 如果转换出错， 返回defaultValue
	 */
	public static Integer parseInteger(String intString, Integer defaultValue) {
		return parseNumber(intString, Integer.class, defaultValue);
	}
}
