package com.ruoyi.retrieve.api;

import com.ruoyi.retrieve.api.domain.LawDoc;

import java.util.List;

/**
 * @author fcs
 * @date 2024/1/31 21:31
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 法律法规RPC远程接口
 */
public interface RemoteLawDocRetrieveService {

    /**
     * 新增
     *
     * @param lawDoc 法律法规索引对象
     * @return 新增条数
     */
    Integer insert(LawDoc lawDoc);

    /**
     * 批量新增
     *
     * @param entityList 法律法规索引对象
     * @return 新增条数
     */
    Integer insertBatch(List<LawDoc> entityList);

    /**
     * 修改
     *
     * @param lawDoc 法律法规索引对象
     * @return 修改条数
     */
    Integer update(LawDoc lawDoc);

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
     * @return 法律法规索引对象
     */
    Boolean exist(Long id);

    /**
     * 根据名称查询
     *
     * @param name 名称
     * @return 法律法规索引对象
     */
    Boolean exist(String name);
}
