package com.ruoyi.common.websocket.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * webSocket配置
 *
 * @author fcs
 * @date 2024/2/26 16:42
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@AutoConfiguration
@EnableWebSocket
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}

