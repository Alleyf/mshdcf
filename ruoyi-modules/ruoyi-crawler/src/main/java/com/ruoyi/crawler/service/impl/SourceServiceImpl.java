package com.ruoyi.crawler.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.crawler.domain.bo.SourceBo;
import com.ruoyi.crawler.domain.vo.SourceVo;
import com.ruoyi.crawler.domain.Source;
import com.ruoyi.crawler.mapper.SourceMapper;
import com.ruoyi.crawler.service.ISourceService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 爬虫数据源Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-26
 */
@RequiredArgsConstructor
@Service
public class SourceServiceImpl implements ISourceService {

    private final SourceMapper baseMapper;

    /**
     * 查询爬虫数据源
     */
    @Override
    public SourceVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询爬虫数据源列表
     */
    @Override
    public TableDataInfo<SourceVo> queryPageList(SourceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Source> lqw = buildQueryWrapper(bo);
        Page<SourceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询爬虫数据源列表
     */
    @Override
    public List<SourceVo> queryList(SourceBo bo) {
        LambdaQueryWrapper<Source> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Source> buildQueryWrapper(SourceBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Source> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getSourceName()), Source::getSourceName, bo.getSourceName());
        lqw.eq(StringUtils.isNotBlank(bo.getSourceUrl()), Source::getSourceUrl, bo.getSourceUrl());
        lqw.eq(bo.getSourceTypeId() != null, Source::getSourceTypeId, bo.getSourceTypeId());
        lqw.eq(bo.getStatus() != null, Source::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增爬虫数据源
     */
    @Override
    public Boolean insertByBo(SourceBo bo) {
        Source add = BeanUtil.toBean(bo, Source.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改爬虫数据源
     */
    @Override
    public Boolean updateByBo(SourceBo bo) {
        Source update = BeanUtil.toBean(bo, Source.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Source entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除爬虫数据源
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
