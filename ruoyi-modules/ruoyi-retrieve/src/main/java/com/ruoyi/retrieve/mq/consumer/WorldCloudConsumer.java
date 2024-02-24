package com.ruoyi.retrieve.mq.consumer;


import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.utils.WorldCloudUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.retrieve.mq.WorldCloudMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * rabbit词云消息队列消费者
 *
 * @author alleyf
 */
@Slf4j
@Component
public class WorldCloudConsumer {

    @Bean
    Consumer<WorldCloudMessage> worldCloud() {
        log.info("初始化词云订阅");
        return obj -> {
//            log.info("消息接收成功：" + obj);
            String worldCloud = WorldCloudUtils.genWorldCloud(obj.getName(), obj.getContent());
//            将词云存入redis
            RedisUtils.setCacheObject(CacheConstants.REDIS_KEY_PREFIX + obj.getId(), worldCloud, Duration.ofMinutes(3));
        };
    }

}
