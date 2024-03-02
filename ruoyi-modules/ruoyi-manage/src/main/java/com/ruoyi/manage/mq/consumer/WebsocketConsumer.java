package com.ruoyi.manage.mq.consumer;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.websocket.websocket.WebSocketService;
import com.ruoyi.manage.enums.SocketMsgType;
import com.ruoyi.manage.mq.WebscoketMessage;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.manage.service.ILawRegulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Consumer;

/**
 * rabbit消息队列消费者
 *
 * @author alleyf
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketConsumer {
    private final IDocCaseService docCaseService;
    private final ILawRegulationService lawRegulationService;


    @Bean
    Consumer<WebscoketMessage> websocket() {
        log.info("初始化订阅");
        return obj -> {
            String jsonMsg = JSONObject.toJSONString(obj);
            log.info("消息接收成功：" + jsonMsg);
            WebSocketService.sendMessage(obj.getClientId(), jsonMsg);
            if (obj.getMsgType().equals(SocketMsgType.CASE.getType())) {
                docCaseService.insertBatch(obj.getClientId());
            } else if (obj.getMsgType().equals(SocketMsgType.LAW.getType())) {
                lawRegulationService.insertBatch(obj.getClientId());
            } else {
                // TODO: 2024/2/27 处理其他消息
            }
        };
    }

}
