package com.ruoyi.retrieve.mq.producer;

import com.ruoyi.retrieve.mq.WorldCloudMessage;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author fcs
 * @date 2024/2/24 13:08
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Component

public class WorldCloudProducer {

    @Resource
    private StreamBridge streamBridge;

    public void sendMsg(Long id, String name, String content, Long delay) {
        // 1. 创建一个自定义消息
        WorldCloudMessage worldCloudMessage = new WorldCloudMessage()
            .setMsgId(UUID.randomUUID().toString())
            .setId(id)
            .setName(name)
            .setContent(content);
        // 2. 消息封装
        Message<WorldCloudMessage> message = MessageBuilder.withPayload(worldCloudMessage)
            .setHeader("x-delay", delay).build();
        // 3. 发送消息
        streamBridge.send("worldCloud-out-0", message);
    }
}
