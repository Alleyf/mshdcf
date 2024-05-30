package com.ruoyi.common.core.constant;

/**
 * @author fcs
 * @date 2024/2/24 21:10
 * @description
 * @since <a href="https://alleyf.github.io">getHelp</a>
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
    String GEMINI_API_URL = "https://geminiprochat.com/api/generate";
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
    String COZE_API_URL = "http://127.0.0.1:9090/v1/chat/completions";
    /**
     * Coze-GPT请求密钥
     */
    String COZE_API_SECRET = "123456";

    /**
     * MoonShot-GPT请求地址
     */
    String MoonShot_API_URL = "https://api.moonshot.cn/v1/chat/completions";
    String Local_MoonShot_API_URL = "http://localhost:9210/v1/chat/completions";

    /**
     * MoonShot-GPT请求密钥
     */
    String MoonShot_API_SECRET = "Bearer sk-3vLlnvvSjZweyMPvhtMZkt5UsLFJ5N1qN7pNpcF94z9ve2Ee"; //sk-oT7gorvy6TuUHv8QZ4OtSlDQnFyBOWfk4JuJh8Rk0NpNcUrB
    String Local_MoonShot_API_SECRET = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ1c2VyLWNlbnRlciIsImV4cCI6MTcxOTU2NDk5OSwiaWF0IjoxNzExNzg4OTk5LCJqdGkiOiJjbzN0N2h1Y3A3ZmN0MHF0djB1ZyIsInR5cCI6InJlZnJlc2giLCJzdWIiOiJjbmM3YzYybG5sOTU3N3VuaWRuMCIsInNwYWNlX2lkIjoiY25jN2M2MmxubDk1Nzd1bmlkbWciLCJhYnN0cmFjdF91c2VyX2lkIjoiY25jN2M2MmxubDk1Nzd1bmlkbTAifQ.AW4vuZ57gAHEp1SIvnhsgQFhccB90FERzn23xiABkV-GWxisam4kLF1VoHXPyHs6UcgyNO4gfjwNnjBJ6Ru4YQ";
}
