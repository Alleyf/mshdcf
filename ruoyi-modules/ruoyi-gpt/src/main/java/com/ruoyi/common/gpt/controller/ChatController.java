package com.ruoyi.common.gpt.controller;

import com.ruoyi.common.gpt.utils.OllamaUtils;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Gpt对话控制器
 *
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @date 2024/3/14 15:51
 */
@RestController
public class ChatController {

    @Resource
    private OllamaUtils ollamaUtils;

    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(value = "message") String message) {
        Map<String, Object> map = new HashMap<>();
        String generate = ollamaUtils.generate(message);
        map.put("code", generate != null ? 200 : 500);
        map.put("msg", generate != null ? "success" : "fail");
        map.put("data", generate);
        return map;
    }

    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message") String message) {
        return ollamaUtils.generateStream(message);
    }
}
