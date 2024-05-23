package com.ruoyi.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.bo.ProcessBo;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 司法案件Service接口
 *
 * @author alleyf
 * @date 2024-01-26
 */
public interface IDocCaseService extends IService<DocCase> {

    /**
     * 查询司法案件
     */
    DocCaseVo queryById(Long id);

    /**
     * 查询司法案件列表
     */
    TableDataInfo<DocCaseVo> queryPageList(DocCaseBo bo, PageQuery pageQuery);

    /**
     * 查询司法案件列表
     */
    List<DocCaseVo> queryList(DocCaseBo bo);

    /**
     * 修改司法案件
     */
    Boolean insertByBo(DocCaseBo bo);

    /**
     * 全量同步到es
     */
    Integer syncAll(String clientId);

    /**
     * 增量同步到es
     */
    Integer syncInc(String clientId);

    /**
     * 修改司法案件
     */
    Boolean updateByBo(DocCaseBo bo);

    /**
     * 校验并批量删除司法案件信息
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

    int process(List<ProcessBo> processList);

    int processContent(List<ProcessBo> processList);

    int processExtra(List<ProcessBo> processList);
}
