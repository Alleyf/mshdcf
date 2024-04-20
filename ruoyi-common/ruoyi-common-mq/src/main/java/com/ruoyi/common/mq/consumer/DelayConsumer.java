package com.ruoyi.common.mq.consumer;


import com.ruoyi.common.mq.Messaging;
import com.ruoyi.websocket.api.RemoteWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * rabbit消息队列消费者
 *
 * @author alleyf
 */
@Slf4j
@Component
public class DelayConsumer {

    @DubboReference
    private RemoteWebSocketService remoteWebSocketService;

    @Bean
    Consumer<Messaging> delay() {
        log.info("初始化订阅");
        return obj -> {
            log.info("消息接收成功：" + obj);
//            发送广播消息
            remoteWebSocketService.sendToAll(obj.getMsgText());
        };
    }

}
