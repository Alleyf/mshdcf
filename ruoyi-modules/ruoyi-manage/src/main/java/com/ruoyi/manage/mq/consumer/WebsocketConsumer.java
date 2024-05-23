package com.ruoyi.manage.mq.consumer;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.manage.enums.SocketMsgType;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.manage.service.ILawRegulationService;
import com.ruoyi.websocket.api.RemoteWebSocketService;
import com.ruoyi.websocket.domain.WebscoketMessage;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class WebsocketConsumer {
    private final IDocCaseService docCaseService;
    private final ILawRegulationService lawRegulationService;
    @DubboReference
    private RemoteWebSocketService remoteWebSocketService;


    @Bean
    Consumer<WebscoketMessage> websocket() {
        log.info("初始化订阅");
        return obj -> {
            String jsonMsg = JSONObject.toJSONString(obj);
            log.info("消息接收成功：{}", jsonMsg);
            remoteWebSocketService.sendToOne(obj.getClientId(), jsonMsg);
            if (obj.getMsgType().equals(SocketMsgType.CASE.getType())) {
                if (obj.getMsgTitle().contains("全量")) {
                    docCaseService.syncAllCase(obj.getClientId());
                } else {
                    docCaseService.syncIncCase(obj.getClientId());
                }
            } else if (obj.getMsgType().equals(SocketMsgType.LAW.getType())) {
                if (obj.getMsgTitle().contains("全量")) {
                    lawRegulationService.syncAll(obj.getClientId());
                } else {
                    lawRegulationService.syncInc(obj.getClientId());
                }
            } else {
                // TODO: 2024/2/27 处理其他消息
            }
        };
    }

}
