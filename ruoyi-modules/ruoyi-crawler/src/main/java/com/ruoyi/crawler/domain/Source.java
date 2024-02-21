package com.ruoyi.crawler.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 爬虫数据源对象 source
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("source")
public class Source extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 数据源名称
     */
    private String sourceName;

    /**
     * 数据源别名
     */
    private String alias;
    /**
     * 数据源授权（0：不需要授权；1：需要授权）
     */
    private Integer authorize;
    /**
     * 数据源url地址
     */
    private String sourceUrl;
    /**
     * 数据源类型（1：司法案例和法律法规；2：司法案例； 3：法律法规）
     */
    private Long sourceTypeId;
    /**
     * 数据源备注说明
     */
    private String remark;
    /**
     * 状态（0：停用；1：正常）
     */
    private Integer status;

}
