package com.ruoyi.crawler.mapper;

import com.ruoyi.crawler.domain.SourceType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 数据源类型表(SourceType)表数据库访问层
 *
 * @author csFan
 * @since 2024-02-21 20:38:45
 */
public interface SourceTypeMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SourceType queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param sourceType 查询条件
     * @param pageable   分页对象
     * @return 对象列表
     */
    List<SourceType> queryAllByLimit(SourceType sourceType, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param sourceType 查询条件
     * @return 总行数
     */
    long count(SourceType sourceType);

    /**
     * 新增数据
     *
     * @param sourceType 实例对象
     * @return 影响行数
     */
    int insert(SourceType sourceType);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SourceType> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SourceType> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SourceType> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<SourceType> entities);

    /**
     * 修改数据
     *
     * @param sourceType 实例对象
     * @return 影响行数
     */
    int update(SourceType sourceType);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

