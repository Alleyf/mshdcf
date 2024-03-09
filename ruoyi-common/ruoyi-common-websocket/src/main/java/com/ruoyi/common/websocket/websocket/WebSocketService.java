package com.ruoyi.common.websocket.websocket;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket服务
 *
 * @author fcs
 * @date 2024/2/26 16:50
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */


@ServerEndpoint(value = "/websocket/{clientId}")
@Component
public class WebSocketService implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    /**
     * 客户端集合
     */
    @JsonBackReference
    private static ConcurrentHashMap<String, WebSocketClient> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收loginId,区分不同客户端用户
     */
    private String clientId = "";

    /**
     * 向指定客户端发送消息
     *
     * @param clientId 客户端id
     * @param message  消息
     */
    public static void sendMessage(String clientId, String message) {
        try {
            if (StringUtils.isNotEmpty(clientId)) {
//            指定发送
                WebSocketClient webSocketClient = webSocketMap.get(clientId);
                if (webSocketClient != null) {
                    webSocketClient.getSession().getBasicRemote().sendText(message);
                }
            } else {
                //广播消息
                for (WebSocketClient webSocketClient : webSocketMap.values()) {
                    if (webSocketClient != null) {
                        webSocketClient.getSession().getBasicRemote().sendText(message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public static ConcurrentHashMap<String, WebSocketClient> getWebSocketMap() {
        return webSocketMap;
    }

    public static void setWebSocketMap(ConcurrentHashMap<String, WebSocketClient> webSocketMap) {
        WebSocketService.webSocketMap = webSocketMap;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) {
        this.session = session;
        this.clientId = clientId;
        WebSocketClient client = new WebSocketClient();
        client.setSession(session);
        client.setUri(session.getRequestURI().toString());
        webSocketMap.put(clientId, client);
        log.info("用户连接:" + clientId + ",当前在线人数为:" + webSocketMap.size());
        try {
            sendMessage("来自后台的反馈：连接成功");
        } catch (IOException e) {
            log.error("用户:" + clientId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("clientId") String clientId) {
        if (webSocketMap.containsKey(clientId)) {
            webSocketMap.remove(clientId);
        }
        log.info(clientId + "用户退出,当前在线人数为:" + webSocketMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户消息:" + clientId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {

        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.clientId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 连接服务器成功后主动推送
     */
    public void sendMessage(String message) throws IOException {
        synchronized (session) {
            this.session.getBasicRemote().sendText(message);
        }
    }

}
