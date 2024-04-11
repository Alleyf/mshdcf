package com.ruoyi.manage.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.annotation.ExcelEnumFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import com.ruoyi.manage.enums.MiningStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author fcs
 * @date 2024/2/1 13:10
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 法律法规导入视图对象
 */
@Data
@ExcelIgnoreUnannotated
public class LawRegulationImportVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 法律法规主键id（雪花id）
     */
//    @ExcelProperty(value = "法律法规主键id", converter = ExcelDictConvert.class)
//    @ExcelDictFormat(readConverterExp = "雪花id")
    private Long id;

    /**
     * 法规名称
     */
    @ExcelProperty(value = "法规名称")
    private String name;

    /**
     * 领域类别
     */
    @ExcelProperty(value = "领域类别")
    private String field;

    /**
     * 法规类型(1：法律 2：行政法规 3：地方性法规 4：司法解释 5：部门规章；6：其他)
     */
    @ExcelProperty(value = "法规类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "law_type")
    private String type;

    /**
     * 原始链接
     */
    @ExcelProperty(value = "原始链接")
    private String url;

    /**
     * 有效性（0：无效 1：有效）
     */
    @ExcelProperty(value = "有效性", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=无效,1=有效")
    private Integer isValidity;

    /**
     * 发布日期
     */
    @ExcelProperty(value = "发布日期")
    private String releaseDate;

    /**
     * 实施日期
     */
    @ExcelProperty(value = "实施日期")
    private String executeDate;

    /**
     * 发布机关
     */
    @ExcelProperty(value = "发布机关")
    private String releaseOrganization;

    /**
     * 法规正文
     */
    @ExcelProperty(value = "正文")
    private String content;
    /**
     * 修正后的法条正文
     */
//    @ExcelProperty(value = "修正正文")
//    private String stripContent;

    /**
     * 附加语义信息（json格式）
     */
    @ExcelProperty(value = "语义信息")
    private String extra;

    /**
     * 法规来源（关联source表主键）
     */
    @ExcelProperty(value = "法规来源", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "crawler_source")
    private Long sourceId;

    /**
     * 法规结构
     */
    @ExcelProperty(value = "法规结构")
    private String structure;

    /**
     * 修改次数
     */
    @ExcelProperty(value = "修改次数")
    private String reviseNum;

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
