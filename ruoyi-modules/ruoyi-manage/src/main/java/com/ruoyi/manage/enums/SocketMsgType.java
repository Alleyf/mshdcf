package com.ruoyi.manage.enums;

import lombok.Getter;

/**
 * @author csFan
 * @date 2024/2/27 14:02
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@Getter
public enum SocketMsgType {
    /**
     * 消息类型
     */
    CASE("司法案件", "case"),
    LAW("法律法规", "law"),
    NORMAL("普通消息", "normal");

    /**
     * 别名
     */
    private final String alias;

    /**
     * 类型
     */
    private final String type;

    SocketMsgType(String alias, String type) {
        this.alias = alias;
        this.type = type;
    }
}
