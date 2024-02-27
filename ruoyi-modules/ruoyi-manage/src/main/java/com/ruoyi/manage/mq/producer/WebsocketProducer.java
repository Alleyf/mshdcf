package com.ruoyi.manage.mq.producer;

import com.ruoyi.manage.mq.WebscoketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 消息生产者
 *
 * @author alleyf
 */
@Component
public class WebsocketProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void sendMsg(String msgType, String msg, String clientId, Long delay) {
        // 构建消息对象
        WebscoketMessage testMessaging = new WebscoketMessage()
            .setMsgId(UUID.randomUUID().toString())
            .setMsgType(msgType)
            .setMsgText(msg)
            .setClientId(clientId);
        Message<WebscoketMessage> message = MessageBuilder.withPayload(testMessaging)
            .setHeader("x-delay", delay).build();
        streamBridge.send("websocket-out-0", message);
    }
}
