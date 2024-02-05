package com.ruoyi.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 司法案例Service接口
 *
 * @author alleyf
 * @date 2024-01-26
 */
public interface IDocCaseService extends IService<DocCase> {

    /**
     * 查询司法案例
     */
    DocCaseVo queryById(Long id);

    /**
     * 查询司法案例列表
     */
    TableDataInfo<DocCaseVo> queryPageList(DocCaseBo bo, PageQuery pageQuery);

    /**
     * 查询司法案例列表
     */
    List<DocCaseVo> queryList(DocCaseBo bo);

    /**
     * 修改司法案例
     */
    Boolean insertByBo(DocCaseBo bo);

    /**
     * 批量插入
     */
    Boolean insertBatch();

    /**
     * 修改司法案例
     */
    Boolean updateByBo(DocCaseBo bo);

    /**
     * 校验并批量删除司法案例信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据主键批量查询
     */
    List<DocCaseVo> queryListByIds(Long[] ids);

    /**
     * 根据案例名称查询
     */
    DocCase selectDocCaseByName(String name);
}
