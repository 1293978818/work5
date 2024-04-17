package com.ygh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 * @author ygh
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();
        serverEndpointExporter.setApplicationContext(applicationContext);
        return serverEndpointExporter;
    }
}
