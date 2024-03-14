package com.ruoyi.common.gpt.utils;

//import com.ruoyi.common.core.utils.SpringUtils;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * ollama对话工具类
 *
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @date 2024/3/14 11:05
 */
@Service
public class OllamaUtils {
    @Resource
    private OllamaChatClient chatClient;

    /**
     * 对话生成文本
     *
     * @param text 输入的文本字符串。
     * @return 根据chatClient的call方法处理后生成的字符串。
     * @description 生成与给定文本相关的某种输出。
     */
    public String generate(String text) {
        // 调用chatClient的call方法，传入text参数
        return chatClient.call(text);
    }

    /**
     * 生成聊天响应的流
     *
     * @param text 用户输入的文本消息，作为聊天的触发点。
     * @return Flux<ChatResponse> 返回一个响应流，包含与用户输入相关的聊天响应。
     */
    public Flux<ChatResponse> generateStream(String text) {
        // 创建一个包含用户消息的Prompt对象
        Prompt prompt = new Prompt(new UserMessage(text));
        // 使用chatClient基于prompt生成一个聊天响应流
        return chatClient.stream(prompt);
    }
}
