package com.ruoyi.crawler.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据源类型表(SourceType)实体类
 *
 * @author csFan
 * @since 2024-02-21 20:39:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("source_type")
public class SourceType extends BaseEntity {
    /**
     * 数据源类型id
     */
    private Long id;
    /**
     * 数据源类型名（司法案例或法律法规）
     */
    private String name;
    /**
     * 数据源英文别名
     */
    private String alias;
    /**
     * 状态（0：停用；1：正常）
     */
    private Integer status;

}

