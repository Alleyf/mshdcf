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
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.retrieve.api.RemoteRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.api.domain.LawDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.domain.bo.LawRegulationBo;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import com.ruoyi.manage.service.ILawRegulationService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 法律法规Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-27
 */
@Service
@Slf4j
public class LawRegulationServiceImpl extends ServiceImpl<LawRegulationMapper, LawRegulation> implements ILawRegulationService {

    @DubboReference(version = "1.0", group = "law")
    public RemoteRetrieveService<LawDoc> remoteRetrieveService;
    @Resource
    private LawRegulationMapper baseMapper;

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
        boolean sqlFlag = baseMapper.insert(add) > 0;
        boolean esFlag = remoteRetrieveService.exist(bo.getId());
        boolean flag = sqlFlag && esFlag;
        if (flag) {
            bo.setId(add.getId());
            //            添加es索引
            LawRegulation newLaw = selectLawRegulationByName(add.getName());
            flag = remoteRetrieveService.insert(BeanCopyUtils.copy(newLaw, LawDoc.class)) > 0;
        }
        return flag;
    }

    /**
     * 批量新增
     */
    @Override
    public Boolean insertBatch() {
        List<LawRegulation> allLaw = baseMapper.selectList();
        log.info("拷贝前的最后一个：" + allLaw.get(allLaw.size() - 1).toString());
        List<LawDoc> all = BeanCopyUtils.copyList(allLaw, LawDoc.class);
        log.info("拷贝后的最后一个：" + all.get(all.size() - 1).toString());
        //        设置mysqlId
        Assert.notNull(all, "数据库暂无案例数据，请先新增数据");
        all = all.stream().peek(lawDoc -> lawDoc.setMysqlId(lawDoc.getId())).collect(Collectors.toList());
        int successNum = 0, insertNum = 300;
        if (all.size() <= insertNum) {
            successNum = remoteRetrieveService.insertBatch(all);
        } else {
//            判断是否为300的整数倍
            boolean remain = all.size() % 300 != 0;
//            查询批量插入总批次
            int epoch = remain ? all.size() / 300 + 1 : all.size() / 300;
            for (int i = 0; i < epoch; i += 1) {
                List<LawDoc> subList;
                if (remain && i == epoch - 1) {
                    subList = all.subList(i * 300, all.size());
                } else {
                    subList = all.subList(i * 300, (i + 1) * 300);
                }
                successNum += remoteRetrieveService.insertBatch(subList);
            }
        }
        return successNum > 0;
    }

    /**
     * 修改法律法规
     */
    @Override
    public Boolean updateByBo(LawRegulationBo bo) {
        LawRegulation update = BeanUtil.toBean(bo, LawRegulation.class);
        validEntityBeforeSave(update);
        boolean sqlFlag = baseMapper.updateById(update) > 0;
        boolean esFlag = remoteRetrieveService.exist(bo.getId());
        boolean flag = sqlFlag && esFlag;
        if (flag) {
//            更新es索引
            flag = remoteRetrieveService.update(BeanCopyUtils.copy(update, LawDoc.class)) > 0;
        }
        return flag;
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
//        if (isValid) {
//            //TODO 做一些业务上的校验,判断是否需要校验
//        }
        boolean flag = baseMapper.deleteBatchIds(ids) > 0;
        if (flag) {
//            删除es索引
            remoteRetrieveService.deleteBatch(ids.toArray(new Long[0]));
        }
        return flag;
    }

    /**
     * 根据案例名字查询
     */
    @Override
    public LawRegulation selectLawRegulationByName(String name) {
        LambdaQueryWrapper<LawRegulation> lqw = Wrappers.lambdaQuery();
        lqw.eq(LawRegulation::getName, name);
        return baseMapper.selectOne(lqw);
    }
}
