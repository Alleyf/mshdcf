package com.ruoyi.common.mq;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Lion Li
 */
@Data
@Accessors(chain = true)
public class Messaging {
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 消息内容
     */
    private String msgText;
}
