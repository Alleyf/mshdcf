package com.ruoyi.retrieve.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.retrieve.api.domain.CaseDoc;

import java.util.Collection;
import java.util.List;

/**
 * @author fcs
 * @date 2024/1/31 16:08
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 司法案件es服务接口
 */
public interface ICaseDocService {

    /**
     * 新增es数据
     *
     * @param caseDoc 司法案件es对象
     * @return Integer
     */
    Integer insert(CaseDoc caseDoc);

    /**
     * 修改es数据
     *
     * @param caseDoc 司法案件es对象
     * @return Integer
     */
    Integer update(CaseDoc caseDoc);

    /**
     * 根据id删除es数据
     *
     * @param id es数据id
     * @return Integer
     */
    Integer delete(Long id);

    /**
     * 根据id集合批量删除es数据
     *
     * @param ids es数据id集合
     * @return Integer
     */
    Integer deleteBatch(Long[] ids);

    /**
     * 根据关键字查询es数据
     *
     * @param keyword 关键字
     * @return CaseDoc
     */
    CaseDoc selectOne(String keyword, Boolean blurSearch);

    /**
     * 根据id查询es数据
     *
     * @param id es数据id
     * @return CaseDoc
     */
    CaseDoc selectById(Long id);

    /**
     * 根据名称查询es数据
     *
     * @param name 名称
     * @return CaseDoc
     */
    CaseDoc selectByName(String name);

    /**
     * 根据关键字查询es数据
     *
     * @param keyword 关键字
     * @return List<CaseDoc>
     */
    List<CaseDoc> selectList(String keyword, Boolean blurSearch);

    /**
     * 查询所有案件列表
     *
     * @return {@link List }<{@link CaseDoc }>
     */
    List<CaseDoc> selectList();

    /**
     * 根据关键字分页查询es数据
     *
     * @param keyword   关键字
     * @param pageQuery 分页参数
     * @return TableDataInfo<CaseDoc>
     */
    TableDataInfo<CaseDoc> selectPage(String keyword, PageQuery pageQuery);

    /**
     * 根据关键字分页查询es数据
     *
     * @param caseDoc   查询条件
     * @param pageQuery 分页参数
     * @return TableDataInfo<CaseDoc>
     */
    TableDataInfo<CaseDoc> selectPage(CaseDoc caseDoc, PageQuery pageQuery);

    /**
     * 根据集合批量新增es数据
     *
     * @param entityList es数据集合
     * @return Integer
     */
    Integer insertBatch(List<CaseDoc> entityList);
}
