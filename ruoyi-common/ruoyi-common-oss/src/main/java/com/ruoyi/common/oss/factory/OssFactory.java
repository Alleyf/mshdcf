package com.ruoyi.common.oss.factory;

import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.oss.constant.OssConstant;
import com.ruoyi.common.oss.core.OssClient;
import com.ruoyi.common.oss.exception.OssException;
import com.ruoyi.common.oss.properties.OssProperties;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件上传Factory
 *
 * @author csFan
 */
@Slf4j
public class OssFactory {

    private static final Map<String, OssClient> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取默认实例
     */
    public static OssClient instance() {
        // 获取redis 默认类型
        String configKey = RedisUtils.getCacheObject(OssConstant.DEFAULT_CONFIG_KEY);
        if (StringUtils.isEmpty(configKey)) {
            throw new OssException("文件存储服务类型无法找到!");
        }
        return instance(configKey);
    }

    /**
     * 根据类型获取实例
     */
    public static synchronized OssClient instance(String configKey) {
        String json = CacheUtils.get(CacheNames.SYS_OSS_CONFIG, configKey);
        if (json == null) {
            throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        OssProperties properties = JsonUtils.parseObject(json, OssProperties.class);
        OssClient client = CLIENT_CACHE.get(configKey);
        if (client == null) {
            CLIENT_CACHE.put(configKey, new OssClient(configKey, properties));
            log.info("创建OSS实例 key => {}", configKey);
            return CLIENT_CACHE.get(configKey);
        }
        // 配置不相同则重新构建
        if (!client.checkPropertiesSame(properties)) {
            CLIENT_CACHE.put(configKey, new OssClient(configKey, properties));
            log.info("重载OSS实例 key => {}", configKey);
            return CLIENT_CACHE.get(configKey);
        }
        return client;
    }

}
