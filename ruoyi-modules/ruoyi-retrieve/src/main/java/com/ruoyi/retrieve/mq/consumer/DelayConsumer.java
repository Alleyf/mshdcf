package com.ruoyi.retrieve.mq.consumer;


import com.ruoyi.retrieve.mq.Messaging;
import lombok.extern.slf4j.Slf4j;
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

    @Bean
    Consumer<Messaging> delay() {
        log.info("初始化订阅");
        return obj -> {
            log.info("消息接收成功：" + obj);
        };
    }

}
