package com.ruoyi.retrieve.api.domain;

import cn.easyes.annotation.*;
import cn.easyes.annotation.rely.Analyzer;
import cn.easyes.annotation.rely.FieldType;
import cn.easyes.annotation.rely.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author fcs
 * @date 2024/1/31 15:37
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 司法案例索引
 */
@IndexName
@Data
public class CaseDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * es中的唯一id
     */
    @IndexId(type = IdType.CUSTOMIZE)
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 案件名称
     */
    @HighLight(preTag = "<text style='color:red'>", postTag = "</text>")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    @NotBlank(message = "标题不能为空")
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
     * 案件内容
     */
    @NotBlank(message = "内容不能为空")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String content;
    /**
     * 判决日期
     */
//    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String judgeDate;
    /**
     * 公开日期
     */
//    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
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
    private String relatedCases;

    @Score
    @IndexField(exist = false)
    private Float score;
}
