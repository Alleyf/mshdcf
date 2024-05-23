package com.ruoyi.retrieve.api;

import java.util.List;

/**
 * @author fcs
 * @date 2024/2/1 11:00
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 司法案件和法律法规es远程调用服务接口
 */
public interface RemoteRetrieveService<T> {
    /**
     * 新增
     *
     * @param doc es文档
     * @return 新增条数
     */
    Integer insert(T doc);

    /**
     * 批量新增
     *
     * @param docList es文档
     * @return 新增条数
     */
    Integer insertBatch(List<T> docList);

    /**
     * 修改
     *
     * @param doc es文档
     * @return 修改条数
     */
    Integer update(T doc);

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
     * @return es文档
     */
    Boolean exist(Long id);

    /**
     * 根据名称查询
     *
     * @param name 名称
     * @return es文档
     */
    Boolean exist(String name);
}
