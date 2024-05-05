package com.ruoyi.common.gpt;

import com.ruoyi.common.gpt.utils.OllamaUtils;
//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @date 2024/3/14 12:22
 */
@SpringBootTest
public class OllamaGptTest {
    @Resource
    private OllamaUtils ollamaUtils;

    @Test
    public void test() {
        String text = "你好，我是小明，我正在使用GPT模型进行聊天。";
        String result = ollamaUtils.generate(text);
        System.out.println(result);
    }
}
