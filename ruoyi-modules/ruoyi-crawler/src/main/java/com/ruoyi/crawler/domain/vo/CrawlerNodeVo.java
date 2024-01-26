package com.ruoyi.crawler.domain.vo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fcs
 * @date 2024/1/24 12:40
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@Data
@Builder
public class CrawlerNodeVo {
    private String _id;
    private String name;
    private String key;
    private String ip;
    private String port;
    private String mac;
    private String hostname;
    private String description;
    private Boolean is_master;
    private String status;
    private Boolean enabled;
    private Boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime active_ts;
    private Integer available_runners;
    private Integer max_runners;


    /**
     * 处理JSON中的日期时间字段的方法
     *
     * @param jsonObject JSON对象
     * @param key        日期时间字段的key
     */
    private static LocalDateTime parseToLocalDateTime(JSONObject jsonObject, String key) {
        // 使用正确的格式器处理ISO 8601格式的UTC日期时间
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));
        String dateTimeStr = jsonObject.getString(key);
        return LocalDateTime.from(formatter.parse(dateTimeStr));
    }


}
