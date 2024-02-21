package com.ruoyi.crawler.service;

import com.ruoyi.crawler.domain.SourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 数据源类型表(SourceType)表服务接口
 *
 * @author makejava
 * @since 2024-02-21 20:39:05
 */
public interface ISourceTypeService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SourceType queryById(Long id);

    /**
     * 分页查询
     *
     * @param sourceType  筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    Page<SourceType> queryByPage(SourceType sourceType, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param sourceType 实例对象
     * @return 实例对象
     */
    SourceType insert(SourceType sourceType);

    /**
     * 修改数据
     *
     * @param sourceType 实例对象
     * @return 实例对象
     */
    SourceType update(SourceType sourceType);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}
