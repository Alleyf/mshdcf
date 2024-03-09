package com.ruoyi.common.mq.producer;

import com.ruoyi.common.mq.Messaging;
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
public class DelayProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void sendMsg(String msg, Long delay) {
        // 构建消息对象
        Messaging testMessaging = new Messaging()
            .setMsgId(UUID.randomUUID().toString())
            .setMsgText(msg);
        Message<Messaging> message = MessageBuilder.withPayload(testMessaging)
            .setHeader("x-delay", delay).build();
        streamBridge.send("delay-out-0", message);
    }
}
