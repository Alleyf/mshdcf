package com.ruoyi.manage.mq;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Lion Li
 */
@Data
@Accessors(chain = true)
public class WebscoketMessage {
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 消息类型
     */
    private String msgType;
    /**
     * 消息内容
     */
    private String msgText;
    /**
     * 客户端id(为空则广播消息)
     */
    private String clientId;
}
