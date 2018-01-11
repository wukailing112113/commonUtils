package com.wins.shop.redis;

public class Constants {
	public static String SUBSCRIBE_CENTER = "";
	// 最大用户登录后最大多长时间下线
	public final static Long MAX_AVALIABLE_INTERVAL = 1800L;
	
	// 管理后台，系统用户登录后最大多长时间下线
	public final static Long SYS_USER_MAX_AVALIABLE_INTERVAL = 1800L;
	
    //app用户登陆后最大多长时间下线(一个月)
	public final static Long MAX_AVALIABLE_INTERVAL_APP = 60*60*24*30L;
	
	public final static Integer MAX_PAGE_SIZE = 1000;
	
	public final static Long  UNPAY_ORDER_EXPIRE = 60*60*24L;

	public final static Integer MAX_PER_GET = 5000;
	
	//分销反利配置id
	public final static Integer CONFIG_ID_REBATE = 10000;
	
	//提现配置id
	public final static Integer CONFIG_ID_CASH = 10001;
	
	//注册页id
	public final static Integer CONFIG_ID_REGISTER = 10002;
	
	//成为合伙人的最底消费金额
	public final static Integer  CONFIG_ID_PARTNER_LOW_LIMIT_AMOUNT = 10003;
	
	//成为合伙人的URL
	public final static Integer  CONFIG_ID_PARTNER_TO_BE_PARENT_URL = 10004;
	
	//成为会员通知推荐人消息模板
	public final static Integer  CONFIG_ID_WX_SEND_SMS_TEMPLATE = 10005;
	
	public final static Integer CONFIG_ID_WX_APPID_APPSECRET = 10006;
	
	//微信公众号URL
	public final static Integer CONFIG_ID_WEIXIN_URL = 10007;
	
	//是否开放注册
	public final static Integer CONFIG_ID_OPEN_REGIST = 10008;
	
	//推广海报分享描述
	public final static Integer CONFIG_ID_PROMOTION_DESC = 10009;
	
	public final static Integer CONFIG_ID_WX_ABOUT_IBA = 10010;
	
	//清除会员的开关
	public final static Integer CONFIG_ID_DEL_MEMBER_KEY = 10011;
	
	//注册成功公众号欢迎语
	public final static Integer CONFIG_ID_REGIST_SUCESS_KEY = 10012;
	
	//关注公众号欢迎语
	public final static Integer CONFIG_ID_FOCUS_WEBCART_KEY = 10013;
	//信息来源38活动
	public final static Byte SRC_38 = 1;
	//信息来源微信商城
	public final static Byte SRC_WX = 2;

	public final static int FLOAT_FIX = 2;

	public final static long SUBS_MAX = 2000;
}
