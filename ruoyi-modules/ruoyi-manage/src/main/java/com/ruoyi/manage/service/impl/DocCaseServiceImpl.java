package com.ruoyi.manage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
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
import java.util.stream.Collectors;

/**
 * 司法案例Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Service
@Slf4j
public class DocCaseServiceImpl extends ServiceImpl<DocCaseMapper, DocCase> implements IDocCaseService {

    @DubboReference(version = "1.0", group = "case", timeout = 200000)
    public RemoteCaseDocRetrieveService remoteCaseRetrieveService;
    @Resource
    private DocCaseMapper baseMapper;

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
        lqw.like(StringUtils.isNotBlank(bo.getCourt()), DocCase::getCourt, bo.getCourt());
        lqw.eq(StringUtils.isNotBlank(bo.getNumber()), DocCase::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getCause()), DocCase::getCause, bo.getCause());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), DocCase::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getProcess()), DocCase::getProcess, bo.getProcess());
        lqw.like(StringUtils.isNotBlank(bo.getLabel()), DocCase::getLabel, bo.getLabel());
        lqw.eq(bo.getSourceId() != null, DocCase::getSourceId, bo.getSourceId());
        lqw.ge(bo.getJudgeDate() != null, DocCase::getJudgeDate, bo.getJudgeDate());
        lqw.le(bo.getPubDate() != null, DocCase::getPubDate, bo.getPubDate());
        lqw.like(StringUtils.isNotBlank(bo.getLegalBasis()), DocCase::getLegalBasis, bo.getLegalBasis());
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
            log.info("新增司法案例成功，id：{}", add.getId());
//            bo.setId(add.getId());
//            添加es索引
            DocCase newCase = selectDocCaseByName(add.getName());
            flag = remoteCaseRetrieveService.insert(BeanCopyUtils.copy(newCase, CaseDoc.class)) > 0;
        }
        return flag;
    }

    @Override
    public Boolean insertBatch() {
        List<DocCase> allCase = baseMapper.selectList();
        List<CaseDoc> all = BeanCopyUtils.copyList(allCase, CaseDoc.class);
//        设置mysqlId
        Assert.notNull(all, "数据库暂无案例数据，请先新增数据");
        all = all.stream().peek(caseDoc -> caseDoc.setMysqlId(caseDoc.getId())).collect(Collectors.toList());
        int successNum = 0, insertNum = 300;
        if (all.size() <= insertNum) {
            successNum = remoteCaseRetrieveService.insertBatch(all);
        } else {
//            判断是否为300的整数倍
            boolean remain = all.size() % 300 != 0;
//            查询批量插入总批次
            int epoch = remain ? all.size() / 300 + 1 : all.size() / 300;
            for (int i = 0; i < epoch; i += 1) {
                List<CaseDoc> subList;
                if (remain && i == epoch - 1) {
                    subList = all.subList(i * 300, all.size());
                } else {
                    subList = all.subList(i * 300, (i + 1) * 300);
                }
                successNum += remoteCaseRetrieveService.insertBatch(subList);
            }
        }
        return successNum > 0;
    }

    /**
     * 修改司法案例
     */
    @Override
    public Boolean updateByBo(DocCaseBo bo) {
        DocCase update = BeanUtil.toBean(bo, DocCase.class);
        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
//            更新es索引
            flag = remoteCaseRetrieveService.update(BeanCopyUtils.copy(update, CaseDoc.class)) > 0;
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DocCase entity) {
        //TODO 做一些数据校验,如唯一约束
        String relatedCasesJson = entity.getRelatedCases();
        if (StringUtils.isBlank(relatedCasesJson)) {
            // 确保字符串是一个有效的JSON对象或数组,允许插入null
            relatedCasesJson = null;
//            relatedCasesJson = "{}"; // 或者提供一个空JSON对象作为默认值
        }
        entity.setRelatedCases(relatedCasesJson);
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
            remoteCaseRetrieveService.deleteBatch(ids.toArray(new Long[0]));
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
