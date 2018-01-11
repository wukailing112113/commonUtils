package com.wins.shop.redis.listener;

public interface KeyExpiredObserver {
	/**
	 * redis键值过期通知
	 * 
	 * @param message
	 *            过期的key名
	 */
	public void updateExpried(String message);
}
