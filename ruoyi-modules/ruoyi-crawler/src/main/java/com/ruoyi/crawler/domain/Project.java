package com.ruoyi.crawler.domain;

import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.autotable.annotation.Ignore;
import com.tangzc.autotable.annotation.mysql.MysqlCharset;
import com.tangzc.autotable.annotation.mysql.MysqlTypeConstant;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.autotable.annotation.UniqueIndex;
import com.tangzc.mpe.base.entity.BaseLogicEntity;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 爬虫项目实体类
 *
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @since 2024/3/23 15:30
 */
@AutoDefine
// 标记生成Mapper和Repository(提供Lambda快捷查询和快捷绑定关联)
@AutoRepository
@MysqlCharset(charset = "utf8mb4", collate = "utf8mb4_general_ci")
@Table(value = "t_project", dsName = "master", comment = "爬虫项目表")
@Data
@EqualsAndHashCode(callSuper = true)
public class Project extends BaseLogicEntity<Long, LocalDateTime> {

    /**
     * 主键
     */
    @ColumnId(type = MysqlTypeConstant.BIGINT, comment = "主键")
    private Long id;
    /**
     * 项目名称
     */
    @Column(comment = "项目名称", type = MysqlTypeConstant.VARCHAR, length = 64, notNull = true)
    @UniqueIndex(name = "inx_name")
    private String name;
    /**
     * 项目描述
     */
    @ColumnComment("项目描述")
    private String description;
    @Ignore // 忽略字段
    private String extra;
}
