package com.ruoyi.manage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.domain.bo.LawRegulationBo;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import com.ruoyi.manage.service.ILawRegulationService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 法律法规Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-27
 */
@RequiredArgsConstructor
@Service
public class LawRegulationServiceImpl implements ILawRegulationService {

    private final LawRegulationMapper baseMapper;

    /**
     * 查询法律法规
     */
    @Override
    public LawRegulationVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询法律法规列表
     */
    @Override
    public TableDataInfo<LawRegulationVo> queryPageList(LawRegulationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<LawRegulation> lqw = buildQueryWrapper(bo);
        Page<LawRegulationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询法律法规列表
     */
    @Override
    public List<LawRegulationVo> queryList(LawRegulationBo bo) {
        LambdaQueryWrapper<LawRegulation> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<LawRegulation> buildQueryWrapper(LawRegulationBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<LawRegulation> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), LawRegulation::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), LawRegulation::getType, bo.getType());
        lqw.eq(bo.getIsValidity() != null, LawRegulation::getIsValidity, bo.getIsValidity());
        lqw.eq(StringUtils.isNotBlank(bo.getReleaseOrganization()), LawRegulation::getReleaseOrganization, bo.getReleaseOrganization());
        lqw.eq(bo.getSourceId() != null, LawRegulation::getSourceId, bo.getSourceId());
        lqw.eq(bo.getStatus() != null, LawRegulation::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增法律法规
     */
    @Override
    public Boolean insertByBo(LawRegulationBo bo) {
        LawRegulation add = BeanUtil.toBean(bo, LawRegulation.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改法律法规
     */
    @Override
    public Boolean updateByBo(LawRegulationBo bo) {
        LawRegulation update = BeanUtil.toBean(bo, LawRegulation.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(LawRegulation entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除法律法规
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
