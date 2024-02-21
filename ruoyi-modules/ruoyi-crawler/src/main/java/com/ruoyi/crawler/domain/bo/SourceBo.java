package com.ruoyi.crawler.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 爬虫数据源业务对象
 *
 * @author alleyf
 * @date 2024-01-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SourceBo extends BaseEntity {

    /**
     * 数据源id
     */
    @NotNull(message = "数据源id不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String sourceName;

    /**
     * 数据源别名
     */
    @NotBlank(message = "数据源别名不能为空", groups = {AddGroup.class, EditGroup.class})
    private String alias;
    /**
     * 数据源授权（0：不需要授权；1：需要授权）
     */
    @NotBlank(message = "数据源是否需要登录不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer authorize;
    /**
     * 数据源url地址
     */
    @NotBlank(message = "数据源url地址不能为空", groups = {AddGroup.class, EditGroup.class})
    private String sourceUrl;

    /**
     * 数据源类型（1：司法案例和法律法规；2：司法案例； 3：法律法规）
     */
    @NotNull(message = "数据源类型（1：司法案例和法律法规；2：司法案例； 3：法律法规）不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long sourceTypeId;

    /**
     * 数据源备注说明
     */
    @NotBlank(message = "数据源备注说明不能为空", groups = {AddGroup.class, EditGroup.class})
    private String remark;

    /**
     * 状态（0：停用；1：正常）
     */
    @NotNull(message = "状态（0：停用；1：正常）不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer status;


}
