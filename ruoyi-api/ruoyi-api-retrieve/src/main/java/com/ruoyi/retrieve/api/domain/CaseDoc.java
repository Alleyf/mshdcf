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
import java.util.List;
import java.util.Map;

/**
 * @author fcs
 * @date 2024/1/31 15:37
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 司法案例索引
 */
@Data
@IndexName(aliasName = "case", maxResultWindow = 1000000)
public class CaseDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * es中的唯一id
     */
    @IndexId(type = IdType.CUSTOMIZE)
//    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * mysql中的唯一id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Long mysqlId;

    /**
     * 案件名称
     */
    @HighLight(mappingField = "highlightName", preTag = "<text style='color:red'>", postTag = "</text>")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
//    @NotBlank(message = "标题不能为空")
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
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String cause;
    /**
     * 文书类型（1：判决书 2：裁定书 3：通知书 4：决定书 5：令 6：其他）
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
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
    @HighLight(mappingField = "highlightContent", preTag = "<text style='color:red'>", postTag = "</text>")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String content;
    /**
     * 判决日期
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd")
    private String judgeDate;
    /**
     * 公开日期
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd")
    private String pubDate;
    /**
     * 法律依据
     */
    private String legalBasis;

    /**
     * 来源id
     */
    private Long sourceId;

    /**
     * 当事人
     */
    private String party;
    /**
     * 相关案件（记录name并以|分隔,应该用json保存,等爬虫重新爬取保存json格式到数据库再替换为map类型）
     */
//    private List<Map<String, Object>> relatedCases;
    private String relatedCases;

    @Score
    @IndexField(exist = false)
    private Float score;

    /**
     * 标题高亮返回值被映射的字段
     */
    @IndexField(exist = false)
    private String highlightName;
    /**
     * 正文高亮返回值被映射的字段
     */
    @IndexField(exist = false)
    private String highlightContent;

    /**
     * 词云图
     */
    private String wordCloud;
}
