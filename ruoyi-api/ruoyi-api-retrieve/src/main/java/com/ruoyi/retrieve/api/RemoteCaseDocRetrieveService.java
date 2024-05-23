package com.ruoyi.retrieve.api;

import com.ruoyi.retrieve.api.domain.CaseDoc;

import java.util.List;

/**
 * @author fcs
 * @date 2024/1/31 21:31
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 司法案件RPC远程接口
 */
public interface RemoteCaseDocRetrieveService {
    /**
     * 查询所有案件列表
     *
     * @return {@link List }<{@link CaseDoc }>
     */
    List<CaseDoc> selectList();

    /**
     * 新增
     *
     * @param caseDoc 司法文书索引对象
     * @return 新增条数
     */
    Integer insert(CaseDoc caseDoc);

    /**
     * 批量新增
     *
     * @param entityList 司法文书索引对象
     * @return 新增条数
     */
    Integer insertBatch(List<CaseDoc> entityList);

    /**
     * 修改
     *
     * @param caseDoc 司法文书索引对象
     * @return 修改条数
     */
    Integer update(CaseDoc caseDoc);

    /**
     * 根据id删除
     *
     * @param id 主键id
     * @return 删除条数
     */
    Integer delete(Long id);

    /**
     * 根据id批量删除
     *
     * @param ids 主键id
     * @return 删除条数
     */
    Integer deleteBatch(Long[] ids);

    /**
     * 根据id查询
     *
     * @param id 主键id
     * @return 司法文书索引对象
     */
    Boolean exist(Long id);

    /**
     * 根据名称查询
     *
     * @param name 名称
     * @return 司法文书索引对象
     */
    Boolean exist(String name);
}
