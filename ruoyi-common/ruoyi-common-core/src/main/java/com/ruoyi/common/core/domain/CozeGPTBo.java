package com.ruoyi.common.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

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
public class CozeGPTBo implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    private static String role = "user";
    private Boolean stream = false;
    private String model = "gpt-4-turbo";
    private List<Map<String, Object>> messages;


    public void setMessages(String prompt, String text) {
        this.messages = new ArrayList<>(1);
        this.messages.add(new HashMap<String, Object>(1) {
            {
                {
                    put("role", CozeGPTBo.role);
                    put("content", prompt + text);
                }
            }
        });
    }
}

