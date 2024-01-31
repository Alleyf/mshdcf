package com.ruoyi.manage.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 法律法规业务对象
 *
 * @author alleyf
 * @date 2024-01-27
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class LawRegulationBo extends BaseEntity {

    /**
     * 法律法规主键id（雪花id）
     */
    @NotNull(message = "法律法规主键id（雪花id）不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 法规名称
     */
    @NotBlank(message = "法规名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 领域类别
     */
//    @NotBlank(message = "领域类别不能为空", groups = {AddGroup.class, EditGroup.class})
    private String field;

    /**
     * 法规类型(1：法律 2：行政法规 3：地方性法规 4：司法解释 5：部门规章；6：其他)
     */
    @NotBlank(message = "法规类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String type;

    /**
     * 原始链接
     */
    @NotBlank(message = "原始链接不能为空", groups = {AddGroup.class, EditGroup.class})
    private String url;

    /**
     * 有效性（0：无效 1：有效）
     */
    @NotNull(message = "有效性不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer isValidity;

    /**
     * 发布日期
     */
    @NotNull(message = "发布日期不能为空", groups = {AddGroup.class, EditGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String releaseDate;

    /**
     * 实施日期
     */
//    @NotNull(message = "实施日期不能为空", groups = {AddGroup.class, EditGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String executeDate;

    /**
     * 发布机关
     */
    @NotBlank(message = "发布机关不能为空", groups = {AddGroup.class, EditGroup.class})
    private String releaseOrganization;

    /**
     * 法规正文
     */
    @NotBlank(message = "法规正文不能为空", groups = {AddGroup.class, EditGroup.class})
    private String content;

    /**
     * 法规来源（关联source表主键）
     */
    @NotNull(message = "法规来源不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long sourceId;

    /**
     * 法规结构
     */
//    @NotBlank(message = "法规结构不能为空", groups = {AddGroup.class, EditGroup.class})
    private String structure;

    /**
     * 修改次数
     */
//    @NotBlank(message = "修改次数不能为空", groups = {AddGroup.class, EditGroup.class})
    private String reviseNum;

    /**
     * 状态（0：停用；1：正常）
     */
    @NotNull(message = "状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer status;


}
