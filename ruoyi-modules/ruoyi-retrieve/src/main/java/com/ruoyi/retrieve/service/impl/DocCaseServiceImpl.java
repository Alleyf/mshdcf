package com.ruoyi.retrieve.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.retrieve.domain.bo.DocCaseBo;
import com.ruoyi.retrieve.domain.vo.DocCaseVo;
import com.ruoyi.retrieve.domain.DocCase;
import com.ruoyi.retrieve.mapper.DocCaseMapper;
import com.ruoyi.retrieve.service.IDocCaseService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 司法案例Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-26
 */
@RequiredArgsConstructor
@Service
public class DocCaseServiceImpl extends ServiceImpl<DocCaseMapper, DocCase> implements IDocCaseService {

    private final DocCaseMapper baseMapper;

    /**
     * 查询司法案例
     */
    @Override
    public DocCaseVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询司法案例列表
     */
    @Override
    public TableDataInfo<DocCaseVo> queryPageList(DocCaseBo bo, PageQuery pageQuery) {
//        构建条件查询修饰器
        LambdaQueryWrapper<DocCase> lqw = buildQueryWrapper(bo);
//        传入分页查询器和条件查询修饰器进行分页查询
        Page<DocCaseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询司法案例列表
     */
    @Override
    public List<DocCaseVo> queryList(DocCaseBo bo) {
        LambdaQueryWrapper<DocCase> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询法案列表
     */
    @Override
    public List<DocCaseVo> queryListByIds(String[] ids) {
        List<Long> idLs = Arrays.stream(ids).map(Long::parseLong).collect(Collectors.toList());
        List<DocCase> list = baseMapper.selectBatchIds(idLs);
        return BeanUtil.copyToList(list, DocCaseVo.class);
    }

    private LambdaQueryWrapper<DocCase> buildQueryWrapper(DocCaseBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DocCase> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), DocCase::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCourt()), DocCase::getCourt, bo.getCourt());
        lqw.eq(StringUtils.isNotBlank(bo.getNumber()), DocCase::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getCause()), DocCase::getCause, bo.getCause());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), DocCase::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getProcess()), DocCase::getProcess, bo.getProcess());
        lqw.eq(StringUtils.isNotBlank(bo.getLabel()), DocCase::getLabel, bo.getLabel());
        lqw.eq(bo.getSourceId() != null, DocCase::getSourceId, bo.getSourceId());
        lqw.eq(bo.getJudgeDate() != null, DocCase::getJudgeDate, bo.getJudgeDate());
        lqw.eq(bo.getPubDate() != null, DocCase::getPubDate, bo.getPubDate());
        lqw.eq(StringUtils.isNotBlank(bo.getLegalBasis()), DocCase::getLegalBasis, bo.getLegalBasis());
        lqw.eq(StringUtils.isNotBlank(bo.getParty()), DocCase::getParty, bo.getParty());
        lqw.eq(bo.getStatus() != null, DocCase::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增司法案例
     */
    @Override
    public Boolean insertByBo(DocCaseBo bo) {
        DocCase add = BeanUtil.toBean(bo, DocCase.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改司法案例
     */
    @Override
    public Boolean updateByBo(DocCaseBo bo) {
        DocCase update = BeanUtil.toBean(bo, DocCase.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DocCase entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除司法案例
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 根据案例名字查询
     */
    @Override
    public DocCase selectDocCaseByName(String name) {
        LambdaQueryWrapper<DocCase> lqw = Wrappers.lambdaQuery();
        lqw.eq(DocCase::getName, name);
        return baseMapper.selectOne(lqw);
    }
}
