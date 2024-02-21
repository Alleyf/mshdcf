package com.ruoyi.crawler.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * 爬虫数据源视图对象
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Data
@ExcelIgnoreUnannotated
public class SourceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源id
     */
    @ExcelProperty(value = "数据源id")
    private Long id;

    /**
     * 数据源名称
     */
    @ExcelProperty(value = "数据源名称")
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
    @ExcelProperty(value = "数据源url地址")
    private String sourceUrl;

    /**
     * 数据源类型（1：司法案例和法律法规；2：司法案例； 3：法律法规）
     */
    @ExcelProperty(value = "数据源类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "crawler_source_type")
    private Long sourceTypeId;

    /**
     * 数据源备注说明
     */
    @ExcelProperty(value = "数据源备注说明")
    private String remark;

    /**
     * 状态（0：停用；1：正常）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "crawl_common_status")
    private Integer status;


}
