package com.ruoyi.retrieve.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.retrieve.api.domain.LawDoc;

import java.util.List;

/**
 * @author fcs
 * @date 2024/2/1 12:30
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
public interface ILawDocService {
    /**
     * 新增es数据
     *
     * @param lawDoc 法律法规es对象
     * @return Integer
     */
    Integer insert(LawDoc lawDoc);

    /**
     * 修改es数据
     *
     * @param lawDoc 法律法规es对象
     * @return Integer
     */
    Integer update(LawDoc lawDoc);

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
     * @return LawDoc
     */
    LawDoc selectOne(String keyword, Boolean blurSearch);

    /**
     * 根据id查询es数据
     *
     * @param id es数据id
     * @return LawDoc
     */
    LawDoc selectById(Long id);

    /**
     * 根据名称查询es数据
     *
     * @param name 名称
     * @return LawDoc
     */
    LawDoc selectByName(String name);

    /**
     * 根据关键字查询es数据
     *
     * @param keyword 关键字
     * @return List<LawDoc>
     */
    List<LawDoc> selectList(String keyword, Boolean blurSearch);

    /**
     * 根据关键字分页查询es数据
     *
     * @param keyword   关键字
     * @param pageQuery 分页参数
     * @return TableDataInfo<LawDoc>
     */
    TableDataInfo<LawDoc> selectPage(String keyword, PageQuery pageQuery);

    /**
     * 根据关键字分页查询es数据
     *
     * @param lawDoc    查询条件
     * @param pageQuery 分页参数
     * @return TableDataInfo<LawDoc>
     */
    TableDataInfo<LawDoc> selectPage(LawDoc lawDoc, PageQuery pageQuery);

    /**
     * 根据集合批量新增es数据
     *
     * @param entityList es数据集合
     * @return Integer
     */
    Integer insertBatch(List<LawDoc> entityList);
}
