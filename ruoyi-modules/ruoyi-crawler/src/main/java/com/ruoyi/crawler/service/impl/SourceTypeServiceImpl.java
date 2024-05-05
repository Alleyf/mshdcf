package com.ruoyi.crawler.service.impl;

import com.ruoyi.crawler.domain.SourceType;
import com.ruoyi.crawler.mapper.SourceTypeMapper;
import com.ruoyi.crawler.service.ISourceTypeService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * 数据源类型表(SourceType)表服务实现类
 *
 * @author makejava
 * @since 2024-02-21 20:39:05
 */
@Service("sourceTypeService")
public class SourceTypeServiceImpl implements ISourceTypeService {
    @Resource
    private SourceTypeMapper sourceTypeMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SourceType queryById(Long id) {
        return this.sourceTypeMapper.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param sourceType  筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @Override
    public Page<SourceType> queryByPage(SourceType sourceType, PageRequest pageRequest) {
        long total = this.sourceTypeMapper.count(sourceType);
        return new PageImpl<>(this.sourceTypeMapper.queryAllByLimit(sourceType, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param sourceType 实例对象
     * @return 实例对象
     */
    @Override
    public SourceType insert(SourceType sourceType) {
        this.sourceTypeMapper.insert(sourceType);
        return sourceType;
    }

    /**
     * 修改数据
     *
     * @param sourceType 实例对象
     * @return 实例对象
     */
    @Override
    public SourceType update(SourceType sourceType) {
        this.sourceTypeMapper.update(sourceType);
        return this.queryById(sourceType.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.sourceTypeMapper.deleteById(id) > 0;
    }
}
