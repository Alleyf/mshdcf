package com.ruoyi.gpt.api;

/**
 * @author fcs
 * @date 2024/2/1 11:00
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
public interface RemoteGptService {

    /**
     * 生成对话
     *
     * @param text 对话请求文本
     * @return String 对话返回结果
     */
    String generate(String text);

}
