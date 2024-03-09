package com.ruoyi.websocket.websocket.dubbo;

import com.ruoyi.websocket.api.RemoteWebSocketService;
import com.ruoyi.websocket.websocket.websocket.WebSocketService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author csFan
 * @date 2024/2/27 0:01
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@Service
@DubboService
public class RemoteWebSocketServiceImpl implements RemoteWebSocketService {
    @Override
    public void sendToAll(String message) {
        WebSocketService.sendMessage(null, message);
    }

    @Override
    public void sendToOne(String message, String clientId) {
        WebSocketService.sendMessage(clientId, message);
    }
}
