package com.ruoyi.crawler.domain;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tangzc.autotable.annotation.mysql.MysqlCharset;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.base.entity.BaseLogicEntity;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author fcs
 * @date 2024/1/24 12:40
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@AutoDefine
// 标记生成Mapper和Repository(提供Lambda快捷查询和快捷绑定关联)
@AutoRepository
@MysqlCharset(charset = "utf8mb4", collate = "utf8mb4_general_ci")
@Table(value = "t_node", dsName = "master", comment = "爬虫节点表")
@Data
@EqualsAndHashCode(callSuper = true)
public class Node extends BaseLogicEntity<Long, LocalDateTime> {
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
