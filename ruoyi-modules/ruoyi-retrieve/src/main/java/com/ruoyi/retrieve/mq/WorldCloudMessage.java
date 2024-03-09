package com.ruoyi.retrieve.mq;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fcs
 * @date 2024/2/24 13:06
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Data
@Accessors(chain = true)
public class WorldCloudMessage implements Serializable {
    private String msgId;
    private Long id;
    private String name;
    private String content;
}
