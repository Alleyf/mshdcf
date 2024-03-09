package com.ruoyi.websocket.websocket.websocket;

import lombok.Data;

import javax.websocket.Session;

/**
 * @author fcs
 * @date 2024/2/26 16:48
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */

public class WebSocketClient {
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 连接的uri
     */
    private String uri;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
