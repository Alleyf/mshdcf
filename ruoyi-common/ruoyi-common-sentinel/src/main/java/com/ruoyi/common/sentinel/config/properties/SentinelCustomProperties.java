package com.ruoyi.common.sentinel.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sentinel自定义配置类
 *
 * @author csFan
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.sentinel.transport")
public class SentinelCustomProperties {

    private String serverName;

}
