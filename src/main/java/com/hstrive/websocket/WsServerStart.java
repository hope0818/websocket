package com.hstrive.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class WsServerStart implements InitializingBean{

	private static Logger logger = LoggerFactory.getLogger(WsServerStart.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		logger.info("wsserver start {}","afterPropertiesSet");
		int port = 18080;
		WebsocketServer server = new WebsocketServer(port);
		server.start();
	}

}
