package com.ruoyi.websocket.dubbo;

import com.ruoyi.websocket.api.RemoteWebSocketService;
import com.ruoyi.websocket.websocket.WebSocketService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author csFan
 * @date 2024/2/27 0:01
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@Service
@DubboService
public class RemoteWebSocketServiceImpl implements RemoteWebSocketService {
    /**
     * 获取在线用户列表
     *
     * @return {@link List }<{@link String }>
     */
    @Override
    public List<String> selectOnlineClientList() {
        List<String> onlineClientList = WebSocketService.selectOnlineClientList();
        return onlineClientList.stream().map(clientId -> clientId.substring(clientId.indexOf(":") + 1)).collect(Collectors.toList());
    }

    /**
     * 向所有连接的客户端发送消息。
     * 该方法会调用WebSocketService的sendMessage方法，将消息发送给所有客户端。
     *
     * @param message 要发送的消息内容。不允许为null。
     */
    @Override
    public void sendToAll(String message) {
        WebSocketService.sendMessage(null, message); // 调用WebSocket服务，向所有客户端发送消息
    }

    /**
     * 将消息发送给指定的客户端。
     *
     * @param clientId 客户端的唯一标识符，用于确定消息的接收方。
     * @param message  要发送的消息内容。
     *                 <p>
     *                 此方法通过调用WebSocketService的sendMessage方法，实现向指定客户端发送消息的功能。
     */
    @Override
    public void sendToOne(String clientId, String message) {
        WebSocketService.sendMessage(clientId, message);
    }
}
