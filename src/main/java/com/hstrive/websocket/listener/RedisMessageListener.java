package com.hstrive.websocket.listener;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hstrive.websocket.WebsocketServer;

@Component
public class RedisMessageListener implements MessageListener{

	private static final Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);
	
	@Override
	public void onMessage(Message message, byte[] pattern) {
		// TODO Auto-generated method stub
		String messageContent = null;
		try {
			messageContent = new String(message.getBody(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String channel = new String(message.getChannel());
		logger.info("messageContent is {}, channel is {}, pattern is {}",messageContent,channel,new String(pattern));
		
		// consumer listener 监听有消息到达,content--可以是唯一标志
		Map<WebSocket,String> map = WebsocketServer.wsUserMap;
		List<WebSocket> wsList = WebsocketServer.wsList;
		logger.info("wsUserMap map:{}",map);
		logger.info("wsList:{}",wsList);
		if(!CollectionUtils.isEmpty(map)){
			// client had established websocket connection and send message
			Set<Entry<WebSocket, String>> entrySet = map.entrySet();
			Iterator<Entry<WebSocket, String>> iterator = entrySet.iterator();
			while(iterator.hasNext()){
				Entry<WebSocket, String> next = iterator.next();
				WebSocket key = next.getKey();
				//String value = next.getValue() != null ? next.getValue() : "";
				
				//logger.info("map key : {}, value:{}", key,value);
				// forEach wsList  类似广播发送消息
				for(WebSocket ws : wsList){
					//logger.info("wsList ws:{}",ws);
					if(ws != key){
						if(ws.isOpen()){
							ws.send("对方发送的消息:" + messageContent);
						}
					}else{
						if(ws.isOpen()){
							ws.send("我发送的消息:" + messageContent);
						}
					}
				}
				// 消息发完之后 remove
				map.remove(key);
			}
		}else{
			if(!CollectionUtils.isEmpty(wsList)){
				// client only had established ws collection,  not send message
				for(WebSocket ws : wsList){
					if(ws.isOpen()){
						ws.send("对方发送消息:" + messageContent);
					}
				}
			}
		}
	}

}
