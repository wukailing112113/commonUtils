package com.wins.shop.util;

public enum Channel {

	//1 PC web, 2 Android app, 3 iOS app， 4 微信
	
	PC((byte)1,"PC web"),
	ANDROID((byte)2,"Android app"),
	IOS((byte)3," iOS app"),
	WEBCHAT((byte)4,"微信");
	
	private Byte code;
	private String desc;
	
	
	private Channel(byte code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public Byte getCode() {
		return code;
	}
	public void setCode(Byte code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	public static Channel getChannel(Byte type) {
		switch (type) {
		case 1:
			return PC;
		case 2:
			return ANDROID;
		case 3:
			return IOS;
		case 4:
			return WEBCHAT;
		default:
			return null;
		}
	}
}
