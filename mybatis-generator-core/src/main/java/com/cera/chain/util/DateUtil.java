package com.cera.chain.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static final String DEFAUL_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String DEFAULT_NO_TIME_FROMAT = "yyyy-MM-dd";

	/**
	 * 获取当前时间点的指定格式字符串
	 * 
	 * @param format
	 *            时间格式
	 * @return 指定格式的字符串
	 */
	public static String getNowStr(String format) {
		Date now = getCurDate();
		return formatDate(now, format);
	}

	/**
	 * 获取当前时间的"yyyy-MM-dd HH:mm:ss"形式字符串
	 * 
	 * @return
	 */
	public static String getNowDefaultFormatStr() {
		return getNowStr(DEFAUL_FORMAT);
	}

	/**
	 * 获取时间的指定格式字符串
	 * 
	 * @param date
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 获取当前时间统一使用该方法，方便后期修改
	 * 
	 * @return
	 */
	public static Date getCurDate() {
		// TODO 分布式系统后修改该时间获取规则
		Date now = new Date();
		return now;
	}

	/**
	 * 获取当前时间戳的string形式（当前时间戳只精确到秒）
	 * 
	 * @return
	 */
	public static String getNowTimeStampStr() {
		long time = getCurDate().getTime();
		return String.valueOf(time / 1000);
	}
	
	public static long getTimeStampStr(Date date){
		return date.getTime() / 1000;
	}
	
	public static Long getCurrentTimeStamp() {
		long time = getCurDate().getTime();
        return time / 1000;
    }

	/**
	 * 以指定的格式是解析时间
	 * 
	 * @param format
	 * @param date
	 * @return
	 */
	public static Date getStrDate(String format, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
		}
		return null;
	}

	/**
	 * 以默认的时间格式解析时间字符串
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStrDateByDefault(String date) {
		if (StringUtil.isEmptyString(date)) {
			return null;
		}
		return getStrDate(DEFAUL_FORMAT, date);
	}

	/**
	 * yyyy-MM-dd 日期转化
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStrDateNoTime(String date) {
		if (StringUtil.isEmptyString(date)) {
			return null;
		}
		return getStrDate(DEFAULT_NO_TIME_FROMAT, date);
	}

	/**
	 * 获取时间的默认格式
	 * 
	 * @param date
	 * @return
	 */
	public static String getDefaultFormatDate(Date date) {
		return formatDate(date, DEFAUL_FORMAT);
	}
	
	public static Date rollDate(Date curDate, char unit, int value){
		Calendar c = Calendar.getInstance();
		c.setTime(curDate);
		
		switch( unit ){
			case 'H' :
				c.add(Calendar.HOUR, value);
				break;
			case 'D' :
				c.add(Calendar.DATE, value);
				break;
		}
		
		curDate = c.getTime();
		return curDate;
	}
	
	/**
	 * 获取指定一天的最后时间
	 * @param date
	 * @return
	 */
	public static Date getDayLastTime(Date date){
		if (null == date){
			return null;
		}
		   Calendar ca = Calendar.getInstance();  
		   ca.setTime(date);
           ca.set(Calendar.HOUR_OF_DAY, 23);  
           ca.set(Calendar.MINUTE, 59);  
           ca.set(Calendar.SECOND, 59); 
           return ca.getTime();
	}

}