package com.ruoyi.manage.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ruoyi.manage.enums.MiningStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.web.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 司法案件对象 doc_case
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doc_case")
public class DocCase extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 案件主键id（雪花id）
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 案件名称
     */
    private String name;
    /**
     * 审判法院
     */
    private String court;
    /**
     * 案号
     */
    private String number;
    /**
     * 原始链接
     */
    private String url;
    /**
     * 案由（1：刑事；2：民事；3：行政；4：国家赔偿；5：执行）
     */
    private String cause;
    /**
     * 文书类型（1：判决书 2：裁定书 3：通知书 4：决定书 5：令 6：其他）
     */
    private String type;
    /**
     * 审理程序
     */
    private String process;
    /**
     * 详细案由
     */
    private String label;
    /**
     * 案件正文
     */
    private String content;
    /**
     * 修正后的案件正文
     */
    private String stripContent;

    /**
     * 语义信息
     */
    private String extra;

    /**
     * 案件来源（关联source表主键）
     */
    private Long sourceId;
    /**
     * 判决日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String judgeDate;
    /**
     * 公开日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String pubDate;
    /**
     * 法律依据
     */
    private String legalBasis;
    /**
     * 当事人
     */
    private String party;
    /**
     * 相关案件（记录name并以|分隔）
     */
//    @TableField(typeHandler = JacksonTypeHandler.class)
//    private List<Map<String, Object>> relatedCases;
    private String relatedCases;
    /**
     * 状态（0：停用；1：正常）
     */
    private Integer status;

    /**
     * 挖掘状态
     */
    private MiningStatus isMining;

}
