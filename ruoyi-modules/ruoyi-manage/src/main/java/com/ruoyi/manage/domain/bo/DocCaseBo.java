package com.ruoyi.manage.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.manage.enums.MiningStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;

import com.ruoyi.common.core.web.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 司法案例业务对象
 *
 * @author alleyf
 * @date 2024-01-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DocCaseBo extends BaseEntity {

    /**
     * 案件主键id（雪花id）
     */
    @NotNull(message = "案件主键id（雪花id）不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 案件名称
     */
    @NotBlank(message = "案件名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 审判法院
     */
    @NotBlank(message = "审判法院不能为空", groups = {AddGroup.class, EditGroup.class})
    private String court;

    /**
     * 案号
     */
    @NotBlank(message = "案号不能为空", groups = {AddGroup.class, EditGroup.class})
    private String number;

    /**
     * 原始链接
     */
//    @URL
//    @NotBlank(message = "原始链接不能为空", groups = {AddGroup.class, EditGroup.class})
    private String url;

    /**
     * 案由（1：刑事；2：民事；3：行政；4：国家赔偿；5：执行）
     */
    @NotBlank(message = "案由（1：刑事；2：民事；3：行政；4：国家赔偿；5：执行）不能为空", groups = {AddGroup.class, EditGroup.class})
    private String cause;

    /**
     * 文书类型（1：判决书 2：裁定书 3：通知书 4：决定书 5：令 6：其他）
     */
    @NotBlank(message = "文书类型（1：判决书 2：裁定书 3：通知书 4：决定书 5：令 6：其他）不能为空", groups = {AddGroup.class, EditGroup.class})
    private String type;

    /**
     * 审理程序
     */
//    @NotBlank(message = "审理程序不能为空", groups = {AddGroup.class, EditGroup.class})
    private String process;

    /**
     * 详细案由
     */
//    @NotBlank(message = "详细案由不能为空", groups = {AddGroup.class, EditGroup.class})
    private String label;

    /**
     * 案件正文
     */
    @NotBlank(message = "案件正文不能为空", groups = {AddGroup.class, EditGroup.class})
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
    @NotNull(message = "案件来源（关联source表主键）不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long sourceId;

    /**
     * 判决日期
     */
    @NotNull(message = "判决日期不能为空", groups = {AddGroup.class, EditGroup.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String judgeDate;

    /**
     * 公开日期
     */
//    @NotNull(message = "公开日期不能为空", groups = {AddGroup.class, EditGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String pubDate;

    /**
     * 法律依据
     */
//    @NotBlank(message = "法律依据不能为空", groups = {AddGroup.class, EditGroup.class})
    private String legalBasis;

    /**
     * 当事人
     */
//    @NotBlank(message = "当事人不能为空", groups = {AddGroup.class, EditGroup.class})
    private String party;

    /**
     * 相关案件（记录name并以|分隔）
     */
//    @NotBlank(message = "相关案件（记录name并以|分隔）不能为空", groups = {AddGroup.class, EditGroup.class})
    private String relatedCases;

    /**
     * 状态（0：停用；1：正常）
     */
    @NotNull(message = "状态（0：停用；1：正常）不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer status;

    /**
     * 挖掘状态
     */
    private Integer isMining;


}
