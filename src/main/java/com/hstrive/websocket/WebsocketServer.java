package com.hstrive.websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.hstrive.websocket.constant.Constants;
import com.hstrive.websocket.util.SpringBeanUtils;

public class WebsocketServer extends WebSocketServer{
	
	private static Logger logger = LoggerFactory.getLogger(WebsocketServer.class);
	
	public static volatile Map<WebSocket, String> wsUserMap = new HashMap<WebSocket, String>();
	
	public static volatile List<WebSocket> wsList = new ArrayList<>();
	
	//@Autowired
	private RedisTemplate<String, String> redisTemplate = SpringBeanUtils.getBean("redisTemplate");
	
	//private RedisTemplate<Object, Object> wsMapTemplate = new RedisTemplate<>();

	public WebsocketServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		//Thread thread = new Thread(new ServerHandlerThread());
		//thread.start();
	}

	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		// TODO Auto-generated method stub
		logger.info("ws arg0 {} is close",arg0);
		if(wsUserMap.containsKey(arg0)){
			wsUserMap.remove(arg0);
		}
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		// TODO Auto-generated method stub
		logger.info("ws {} error, arg1:{}", arg0,arg1);
	}

	@Override
	public void onMessage(WebSocket arg0, String arg1) {
		// TODO Auto-generated method stub
		logger.info("receive message from {} , content:{}", arg0, arg1);
		wsUserMap.put(arg0, arg1);
		//arg0.send(arg1);
		
		logger.info("redisTemplate:{}",redisTemplate);
		/*try {
			Map<Object,Object> wsMap = wsMapTemplate.opsForHash().entries("wsMap"); 
			wsMapTemplate.opsForHash().putAll("wsMap", wsUserMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		
		// producer publish message
		redisTemplate.convertAndSend(Constants.CHANNEL, arg1);
	}

	@Override
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		// TODO Auto-generated method stub
		logger.info("ws {} is open " , arg0);
		wsList.add(arg0);
	}

}
