package com.ruoyi.manage.domain.vo;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;


/**
 * 法律法规视图对象
 *
 * @author alleyf
 * @date 2024-01-27
 */
@Data
@ExcelIgnoreUnannotated
public class LawRegulationVo implements Serializable {

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
    @ExcelProperty(value = "法规类型(1：法律 2：行政法规 3：地方性法规 4：司法解释 5：部门规章；6：其他)", converter = ExcelDictConvert.class)
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
    @ExcelDictFormat(readConverterExp = "0=：无效,1=：有效")
    private Integer isValidity;

    /**
     * 发布日期
     */
    @ExcelProperty(value = "发布日期")
    private Date releaseDate;

    /**
     * 实施日期
     */
    @ExcelProperty(value = "实施日期")
    private Date executeDate;

    /**
     * 发布机关
     */
    @ExcelProperty(value = "发布机关")
    private String releaseOrganization;

    /**
     * 法规正文
     */
    @ExcelProperty(value = "法规正文")
    private String content;

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
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;


}
