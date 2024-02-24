package com.ruoyi.common.core.constant;

/**
 * @author fcs
 * @date 2024/2/24 21:10
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
public interface GPTConstants {
    /**
     * 代理地址
     */
    String PROXY_HOST = "127.0.0.1";
    /**
     * 代理端口
     */
    int PROXY_PORT = 7890;
    /**
     * 官方代理Gemini-Pro请求地址
     */
    String GEMINI_API_URL = "https://gemini.gxnas.eu.org/api/generate";
    /**
     * 虾壳Gemini-Pro请求地址
     */
    String GEMINI_XIAKE_API_URL = "https://chat.xiake.pro/api/google/v1beta/models/gemini-pro:generateContent";
    /**
     * 虾壳Gemini-Pro请求密钥
     */
    String GEMINI_XIAKE_API_SECRET = "Bearer AIzaSyBUocEOiA9V5BTZuUu5g_CWmfsYmqDjW_s";

    /**
     * Coze-GPT请求地址
     */
    String COZE_API_URL = "http://103.225.198.88:7077/v1/chat/completions";
    /**
     * Coze-GPT请求密钥
     */
    String COZE_API_SECRET = "coze123456";
}
