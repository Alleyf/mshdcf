package com.ruoyi.retrieve.api.domain;

import org.dromara.easyes.annotation.*;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author fcs
 * @date 2024/2/1 11:10
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 法律法规索引
 * <p>keyword: 精确匹配(eq)or模糊匹配(like);</p>
 * <p>text: 模糊匹配(match or query_string);</p>
 */
@Data
@IndexName(aliasName = "law", maxResultWindow = 1000000)
public class LawDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * es中的唯一id
     */
    @IndexId(type = IdType.CUSTOMIZE)
    private Long id;

    /**
     * mysql中的唯一id
     */
    @IndexField(fieldType = FieldType.KEYWORD, value = "mId")
    private Long mysqlId;

    /**
     * 案件名称
     */
    @HighLight(mappingField = "highlightName", preTag = "<text style='color:red'>", postTag = "</text>")
    @IndexField(fieldType = FieldType.KEYWORD, analyzer = Analyzer.NONE, searchAnalyzer = Analyzer.NONE)
//    @IndexField(fieldType = FieldType.KEYWORD_TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String name;
    /**
     * 所属领域
     */
    private String field;

    /**
     * 原始链接
     */
    private String url;

    /**
     * 法规类型(1：法律 2：行政法规 3：地方性法规 4：司法解释 5：部门规章；6：其他)
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String type;

    /**
     * 案件内容
     */
    @HighLight(mappingField = "highlightContent", preTag = "<text style='color:red'>", postTag = "</text>")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String content;
    /**
     * 修正法条内容
     */
    @HighLight(mappingField = "highlightStripContent", preTag = "<text style='color:red'>", postTag = "</text>")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String stripContent;
    /**
     * 附加语义信息（json格式）
     */
    @IndexField(fieldType = FieldType.TEXT)
    private String extra;
    /**
     * 判决日期
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd")
    private String releaseDate;
    /**
     * 公开日期
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd")
    private String executeDate;

    /**
     * 发文机关
     */
    private String releaseOrganization;

    /**
     * 法律结构
     */
    private String structure;

    /**
     * 修订次数
     */
    private String reviseNum;

    /**
     * 来源id
     */
    private Long sourceId;

    /**
     * 是否有效
     */
    private Long isValidity;

    /**
     * 查询评分
     */
    @Score
    @IndexField(exist = false)
    private Float score;

    /**
     * 标题高亮返回值被映射的字段
     */
    @IndexField(exist = false)
    private String highlightName;
//    /**
//     * 正文高亮返回值被映射的字段
//     */
//    @IndexField(exist = false)
//    private String highlightContent;
    /**
     * 修正正文高亮返回值被映射的字段
     */
    @IndexField(exist = false)
    private String highlightStripContent;


}
