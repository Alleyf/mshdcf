package com.ruoyi.common.core.web.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree基类
 *
 * @author csFan
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TreeEntity<T> extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 子树
     */
    @TableField(exist = false)
    private List<T> children = new ArrayList<>();

}
