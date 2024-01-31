package com.ruoyi.manage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.retrieve.api.RemoteCaseDocService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.service.IDocCaseService;

import javax.annotation.Resource;
import java.util.*;

/**
 * 司法案例Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Service
@Slf4j
public class DocCaseServiceImpl extends ServiceImpl<DocCaseMapper, DocCase> implements IDocCaseService {

    @Resource
    private DocCaseMapper baseMapper;
    @DubboReference
    private RemoteCaseDocService<CaseDoc> remoteCaseDocService;

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
    public List<DocCaseVo> queryListByIds(Long[] ids) {
        List<Long> idLs = Arrays.asList(ids);
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
        boolean sqlFlag = baseMapper.insert(add) > 0;
        boolean esFlag = remoteCaseDocService.exist(bo.getId());
        boolean flag = sqlFlag && esFlag;
        if (flag) {
            log.info("新增司法案例成功，id：{}", add.getId());
            bo.setId(add.getId());
//            添加es索引
            DocCase newCase = selectDocCaseByName(add.getName());
            flag = remoteCaseDocService.insert(BeanCopyUtils.copy(newCase, CaseDoc.class)) > 0;
        }
        return flag;
    }

    @Override
    public Boolean insertBatch() {
        List<DocCase> allCase = baseMapper.selectList();
        List<CaseDoc> allCaseDoc = BeanCopyUtils.copyList(allCase, CaseDoc.class);
        return remoteCaseDocService.insertBatch(allCaseDoc) > 0;
    }

    /**
     * 修改司法案例
     */
    @Override
    public Boolean updateByBo(DocCaseBo bo) {
        DocCase update = BeanUtil.toBean(bo, DocCase.class);
        validEntityBeforeSave(update);
        boolean sqlFlag = baseMapper.updateById(update) > 0;
        boolean esFlag = remoteCaseDocService.exist(bo.getId());
        boolean flag = sqlFlag && esFlag;
        if (flag) {
//            更新es索引
            flag = remoteCaseDocService.update(BeanCopyUtils.copy(update, CaseDoc.class)) > 0;
        }
        return flag;
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
//        if (isValid) {
//            //TODO 做一些业务上的校验,判断是否需要校验
//        }
        boolean flag = baseMapper.deleteBatchIds(ids) > 0;
        if (flag) {
//            删除es索引
            remoteCaseDocService.deleteBatch(ids.toArray(new Long[0]));
        }
        return flag;
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
