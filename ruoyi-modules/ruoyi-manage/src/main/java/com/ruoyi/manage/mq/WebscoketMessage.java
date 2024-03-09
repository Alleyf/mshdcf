package com.ruoyi.manage.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Lion Li
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
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
     * 消息标题
     */
    private String msgTitle;
    /**
     * 消息内容
     */
    private String msgText;
    /**
     * 客户端id(为空则广播消息)
     */
    private String clientId;
}
