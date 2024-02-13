package com.ruoyi.manage.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 司法案例数据智能处理业务请求对象
 *
 * @author fcs
 * @date 2024/2/13 12:29
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DocCaseProcessBo extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 案件主键id（雪花id）
     */
    @NotNull(message = "案件主键id（雪花id）不能为空", groups = {EditGroup.class})
    private Long id;
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
}
