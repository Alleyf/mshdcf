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
    private String id;
    private String name;
    private String key;
    private String ip;
    private String port;
    private String mac;
    private String hostname;
    private String description;
    private Boolean isMaster;
    private String status;
    private Boolean enabled;
    private Boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activeTs;
    private Integer availableRunners;
    private Integer maxRunners;

    /**
     * 将JSON数组转换为CrawlerNode列表
     *
     * @param jsonArray JSON数组
     * @return CrawlerNode列表
     */
    public static List<CrawlerNodeVo> map(JSONArray jsonArray) {
        return jsonArray.toJavaList(JSONObject.class).stream().map(CrawlerNodeVo::getCrawlerNode).collect(Collectors.toList());
    }

    /**
     * 将JSON对象转换为CrawlerNode
     *
     * @param jsonObject JSON对象
     * @return CrawlerNode
     */
    public static CrawlerNodeVo map(JSONObject jsonObject) {
        return getCrawlerNode(jsonObject);
    }

    private static CrawlerNodeVo getCrawlerNode(JSONObject jsonObject) {
        return CrawlerNodeVo.builder()
            .id(jsonObject.getString("_id"))
            .name(jsonObject.getString("name"))
            .key(jsonObject.getString("key"))
            .ip(jsonObject.getString("ip"))
            .port(jsonObject.getString("port"))
            .mac(jsonObject.getString("mac"))
            .hostname(jsonObject.getString("hostname"))
            .description(jsonObject.getString("description"))
            .isMaster(jsonObject.getBoolean("is_master"))
            .status(jsonObject.getString("status"))
            .enabled(jsonObject.getBoolean("enabled"))
            .active(jsonObject.getBoolean("active"))
            // 对于日期时间类型的字段，需要使用Fastjson或其他库提供的相应方法进行转换
            .activeTs(parseToLocalDateTime(jsonObject, "active_ts"))
            .availableRunners(jsonObject.getInteger("available_runners"))
            .maxRunners(jsonObject.getInteger("max_runners"))
            .build();
    }


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
