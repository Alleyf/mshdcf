package com.ruoyi.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @date 2024/3/14 17:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoonShotBo implements Serializable {
    private static final long serialVersionUID = 1L;
    // 定义Moonshot AI模型的属性
    private String model = "moonshot-v1-8k";
    private List<Message> messages;
    private double temperature = 0.4;

    // 示例：根据用户提供的内容创建并初始化一个MoonShotBo实例
    public static MoonShotBo test() {
        List<MoonShotBo.Message> messageList = new ArrayList<>();
        messageList.add(new MoonShotBo.Message("system", "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"));
        messageList.add(new MoonShotBo.Message("user", "你好，我叫李雷，1+1等于多少？"));
        return new MoonShotBo("moonshot-v1-8k", messageList, 0.4);
        // 这里可以对moonShotBo进行进一步的操作，例如回答用户问题
        // ...
    }

    public void setContext(String prompt, String text) {
        List<MoonShotBo.Message> messageList = new ArrayList<>();
        messageList.add(new MoonShotBo.Message("system", prompt));
        messageList.add(new MoonShotBo.Message("user", text));
        this.messages = messageList;
    }

    // 用户消息对应的实体类
    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}

