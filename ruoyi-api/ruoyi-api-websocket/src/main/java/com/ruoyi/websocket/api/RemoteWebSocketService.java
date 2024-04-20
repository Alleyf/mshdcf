package com.ruoyi.websocket.api;

/**
 * websocket远程服务
 *
 * @author fcs
 * @date 2024/2/26 23:53
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
public interface RemoteWebSocketService {
    /**
     * 发送消息到所有在线的客户端
     *
     * @param message 消息内容
     */
    void sendToAll(String message);

    /**
     * 发送消息到指定的客户端
     *
     * @param message  消息内容
     * @param clientId 客户端id
     */
    void sendToOne(String clientId, String message);
}
