package com.hstrive.websocket;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class ServerHandlerThread implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(ServerHandlerThread.class);
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			/**
			 * 类似广播
			 */
			Map<WebSocket, String> map = WebsocketServer.wsUserMap;
			List<WebSocket> wsList = WebsocketServer.wsList;
			if(!CollectionUtils.isEmpty(map)){
				//logger.info("map:{}",map);
				Set<Entry<WebSocket, String>> entrySet = map.entrySet();
				for(Entry<WebSocket, String> entry : entrySet){
					WebSocket key = entry.getKey();
					String value = entry.getValue();
					
					// 遍历所有的在线连接
					for(WebSocket ws : wsList){
						if(ws != key){
							if(ws.isOpen() && !StringUtils.isEmpty(value)){
								//key.send(value);
								logger.info("map:{}",map);
								ws.send("对方发送消息:"+value);
								map.remove(key);
							}
						}else{
							if(ws.isOpen() && !StringUtils.isEmpty(value)){
								//key.send(value);
								logger.info("map:{}",map);
								ws.send("我发送的消息:"+value);
							}
						}
					}
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
