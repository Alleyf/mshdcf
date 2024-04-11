package com.ruoyi.manage.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.annotation.ExcelEnumFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import com.ruoyi.manage.enums.MiningStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;


/**
 * 司法案例导入视图对象
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Data
@ExcelIgnoreUnannotated
public class DocCaseImportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 案件主键id（雪花id）
     */
//    @ExcelProperty(value = "案件序号")
    private Long id;

    /**
     * 案件名称
     */
    @ExcelProperty(value = "案件名称")
    private String name;

    /**
     * 审判法院
     */
    @ExcelProperty(value = "审判法院")
    private String court;

    /**
     * 案号
     */
    @ExcelProperty(value = "案号")
    private String number;

    /**
     * 原始链接
     */
    @ExcelProperty(value = "原始链接")
    private String url;

    /**
     * 案由（1：刑事；2：民事；3：行政；4：国家赔偿；5：执行）
     */
    @ExcelProperty(value = "案由", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "doc_case_cause")
    private String cause;

    /**
     * 文书类型（1：判决书 2：裁定书 3：通知书 4：决定书 5：令 6：其他）
     */
    @ExcelProperty(value = "文书类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "doc_case_type")
    private String type;

    /**
     * 审理程序
     */
    @ExcelProperty(value = "审理程序")
    private String process;

    /**
     * 详细案由
     */
    @ExcelProperty(value = "详细案由")
    private String label;

    /**
     * 案件来源（关联source表主键）
     */
    @ExcelProperty(value = "案件来源", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "crawler_source")
    private Long sourceId;

    /**
     * 判决日期
     */
    @ExcelProperty(value = "判决日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String judgeDate;

    /**
     * 公开日期
     */
    @ExcelProperty(value = "公开日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String pubDate;

    /**
     * 法律依据
     */
    @ExcelProperty(value = "法律依据")
    private String legalBasis;

    /**
     * 当事人
     */
    @ExcelProperty(value = "当事人")
    private String party;

    /**
     * 案件正文
     */
    @ExcelProperty(value = "正文")
    private String content;
    /**
     * 修正后的案件正文
     */
//    @ExcelProperty(value = "修正正文")
//    private String stripContent;

    /**
     * 附加语义信息（json格式）
     */
    @ExcelProperty(value = "语义信息")
    private String extra;

    /**
     * 状态（0：停用；1：正常）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "crawl_common_status")
    private Integer status;

    /**
     * 挖掘状态
     */
    @ExcelProperty(value = "挖掘状态", converter = ExcelDictConvert.class)
    @ExcelEnumFormat(enumClass = MiningStatus.class, textField = "message")
    private Integer isMining;
}
