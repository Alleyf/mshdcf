package com.ruoyi.manage.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ruoyi.manage.enums.MiningStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Map;

import com.ruoyi.common.core.web.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 法律法规对象 law_regulation
 *
 * @author alleyf
 * @date 2024-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("law_regulation")
public class LawRegulation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 法律法规主键id（雪花id）
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 法规名称
     */
    private String name;
    /**
     * 领域类别
     */
    private String field;
    /**
     * 法规类型(1：法律 2：行政法规 3：地方性法规 4：司法解释 5：部门规章；6：其他)
     */
    private String type;
    /**
     * 原始链接
     */
    private String url;
    /**
     * 有效性（0：无效 1：有效）
     */
    private Integer isValidity;
    /**
     * 发布日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String releaseDate;
    /**
     * 实施日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String executeDate;
    /**
     * 发布机关
     */
    private String releaseOrganization;
    /**
     * 法条正文
     */
    private String content;
    /**
     * 修正后的法条正文
     */
    private String stripContent;

    /**
     * 语义信息
     */
    private String extra;
    /**
     * 法规来源（关联source表主键）
     */
    private Long sourceId;
    /**
     * 法规结构
     */
    private String structure;
    /**
     * 修改次数
     */
    private String reviseNum;
    /**
     * 状态（0：停用；1：正常）
     */
    private Integer status;
    /**
     * 挖掘状态
     */
    private MiningStatus isMining;
}
