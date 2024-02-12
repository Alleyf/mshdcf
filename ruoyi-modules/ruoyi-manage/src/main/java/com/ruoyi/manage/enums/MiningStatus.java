package com.ruoyi.manage.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author fcs
 * @date 2024/2/12 12:42
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Getter
public enum MiningStatus {
    // 0：未清洗挖掘，1：已清洗，2：已挖掘
    ORIGIN(0, "未清洗挖掘"),
    STRIPED(1, "已清洗"),
    MININGED(2, "已挖掘");

    /**
     * 挖掘状态码
     */
    @EnumValue
    private final int code;

    /**
     * 挖掘状态描述
     */
    private final String message;

    MiningStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
