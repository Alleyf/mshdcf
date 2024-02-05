package com.ruoyi.manage.config;

import com.hankcs.hanlp.restful.HanLPClient;
import com.ruoyi.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fcs
 * @date 2024/1/31 12:00
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Configuration
@Slf4j
public class HanLPConfig {
    @Value("${hanlp.url}")
    String url;

    @Value("${hanlp.token}")
    String token;

    @Bean
    public HanLPClient hanLPClient() {
        if (token == null || "null".equals(token) || StringUtils.isEmpty(token)) {
            log.info("token {} is null", token);
            return new HanLPClient(url, null);
        }
        return new HanLPClient(url, token, "zh", 100000);
    }
}
