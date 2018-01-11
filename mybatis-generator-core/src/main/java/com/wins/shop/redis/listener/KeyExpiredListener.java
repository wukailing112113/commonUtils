package com.wins.shop.redis.listener;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Key 事件监听器，收到事件监听后，派发给业务线程去处理
 * 
 * @author chenzhm
 *
 */
public class KeyExpiredListener implements MessageListener {
	Logger logger = Logger.getLogger(KeyExpiredListener.class);
	
	private Map<String, KeyExpiredObserver> observers;

	public Map<String, KeyExpiredObserver> getObservers() {
		return observers;
	}

	public void setObservers(Map<String, KeyExpiredObserver> observers) {
		this.observers = observers;
	}

	public void onMessage(Message message, byte[] pattern) {
		StringRedisSerializer strSerial = new StringRedisSerializer();
		String msgBody = strSerial.deserialize(message.getBody());
		String channel = strSerial.deserialize(message.getChannel());
		String strPatt = strSerial.deserialize(pattern);
		logger.debug("onPMessage pattern " + strPatt+ " " + channel + " " + msgBody);
		Set<String> keyPatterns = observers.keySet();
		for (String p: keyPatterns) {
			if(msgBody.matches(p)){
				KeyExpiredObserver observer = observers.get(p);
				observer.updateExpried(msgBody);
			}
		}
	}
	
}
