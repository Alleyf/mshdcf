package com.ruoyi.common.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import okhttp3.internal.ws.RealWebSocket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini-Pro业务请求对象
 *
 * @author fcs
 * @date 2024/2/24 19:57
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Data
public class GeminiGPTBo implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    private static String role = "user";
    @JsonIgnore
    private static Float temperature = 0.6F;
    @JsonIgnore
    private static Integer maxOutputTokens = 10000;
    @JsonIgnore
    private static Integer topP = 1;
    List<Map<String, Object>> contents;
    Map<String, Object> generationConfig;
    @JsonIgnore
    List<Map<String, Object>> parts;

    public GeminiGPTBo() {
        this.generationConfig = new HashMap<>(3);
        this.generationConfig.put("temperature", GeminiGPTBo.temperature);
        this.generationConfig.put("maxOutputTokens", GeminiGPTBo.maxOutputTokens);
        this.generationConfig.put("topP", GeminiGPTBo.topP);
    }

    public void setContents(String prompt, String text) {
        this.contents = new ArrayList<>(1);
        this.parts = new ArrayList<>(2);
        this.parts.add(new HashMap<String, Object>(1) {
            {
                {
                    put("text", prompt);
                }
            }
        });
        this.parts.add(new HashMap<String, Object>(1) {
            {
                {
                    put("text", text);
                }
            }
        });
        HashMap<String, Object> contentMap = new HashMap<>(1);
        contentMap.put("role", GeminiGPTBo.role);
        contentMap.put("parts", this.parts);
        this.contents.add(contentMap);
    }
}

