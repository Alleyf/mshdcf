package com.ruoyi.manage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.manage.domain.bo.LawRegulationBo;
import com.ruoyi.manage.domain.bo.ProcessBo;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.enums.MiningStatus;
import com.ruoyi.manage.enums.SocketMsgType;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import com.ruoyi.manage.service.ILawRegulationService;
import com.ruoyi.retrieve.api.RemoteLawDocRetrieveService;
import com.ruoyi.retrieve.api.domain.LawDoc;
import com.ruoyi.websocket.api.RemoteWebSocketService;
import com.ruoyi.websocket.domain.WebscoketMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 法律法规Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-27
 */
@Service
@Slf4j
public class LawRegulationServiceImpl extends ServiceImpl<LawRegulationMapper, LawRegulation> implements ILawRegulationService {

    @DubboReference(version = "1.0", group = "law", timeout = 200000)
    public RemoteLawDocRetrieveService remoteLawRetrieveService;
    @DubboReference
    private RemoteWebSocketService remoteWebSocketService;
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
        lqw.eq(ObjectUtil.isNotNull(bo.getIsMining()), LawRegulation::getIsMining, MiningStatus.getMiningStatus(bo.getIsMining()));
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
        lqw.eq(bo.getIsMining() != null, LawRegulation::getIsMining, MiningStatus.getMiningStatus(bo.getIsMining()));
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
            //            添加es索引
            LawRegulation newLaw = selectLawRegulationByName(add.getName());
            flag = remoteLawRetrieveService.insert(BeanCopyUtils.copy(newLaw, LawDoc.class)) > 0;
        }
        return flag;
    }

    /**
     * 批量新增
     */
//    @Override
//    public Integer insertBatch(String clientId) {
////        String clientId = LoginHelper.getLoginId();
//        List<LawRegulation> allLaw = baseMapper.selectList();
//        List<LawDoc> all = BeanCopyUtils.copyList(allLaw, LawDoc.class);
//        int allLawSize = allLaw.size();
//        //        设置mysqlId
//        Assert.notNull(all, "数据库暂无案例数据，请先新增数据");
//        all = all.stream().peek(lawDoc -> lawDoc.setMysqlId(lawDoc.getId())).collect(Collectors.toList());
//        int successNum = 0, insertNum = 100;
//        if (allLawSize <= insertNum) {
//            successNum = remoteLawRetrieveService.insertBatch(all);
//            //            发送同步进展消息
//            WebscoketMessage message = new WebscoketMessage(IdUtil.simpleUUID(), SocketMsgType.LAW.getType(), "全量同步法律法规", successNum + "/" + allLawSize, clientId);
//            WebSocketService.sendMessage(clientId, JSONObject.toJSONString(message));
//        } else {
////            判断是否为300的整数倍
//            boolean remain = all.size() % insertNum != 0;
////            查询批量插入总批次
//            int epoch = remain ? all.size() / insertNum + 1 : all.size() / insertNum;
//            for (int i = 0; i < epoch; i += 1) {
//                List<LawDoc> subList;
//                if (remain && i == epoch - 1) {
//                    subList = all.subList(i * insertNum, all.size());
//                } else {
//                    subList = all.subList(i * insertNum, (i + 1) * insertNum);
//                }
//                successNum += remoteLawRetrieveService.insertBatch(subList);
//                //            发送同步进展消息
//                WebscoketMessage message = new WebscoketMessage(IdUtil.simpleUUID(), SocketMsgType.LAW.getType(), "全量同步法律法规", successNum + "/" + allLawSize, clientId);
//                WebSocketService.sendMessage(clientId, JSONObject.toJSONString(message));
//            }
//        }
//        return successNum;
//    }

    /**
     * 批量添加法律法规到es
     */
    @Override
    public Integer insertBatch(String clientId) {
        // 验证clientId的合法性
        Assert.notNull(clientId, "clientId不能为空");

        List<LawRegulationVo> lawRegulations = this.queryList(new LawRegulationBo());

        // 当查询结果为空时，直接返回0或抛出异常，避免后续逻辑执行
        if (lawRegulations.isEmpty()) {
            return 0;
        }

        List<LawDoc> lawDocs = BeanCopyUtils.copyList(lawRegulations, LawDoc.class);
        int allLawSize = lawRegulations.size();

        // 设置mysqlId
        if (lawDocs != null) {
            lawDocs.forEach(lawDoc -> lawDoc.setMysqlId(lawDoc.getId()));
        }

        int insertNum = 300; // 批量插入数
        int successNum = 0;

        // 分块同步
        if (allLawSize <= insertNum) {
            successNum = remoteLawRetrieveService.insertBatch(lawDocs);
            sendMessage(clientId, successNum, allLawSize);
        } else {
            int epoch = allLawSize / insertNum + (allLawSize % insertNum != 0 ? 1 : 0);
            for (int i = 0; i < epoch; i++) {
                List<LawDoc> subList = null;
                if (lawDocs != null) {
                    subList = (i == epoch - 1) ?
                        lawDocs.subList(i * insertNum, allLawSize) :
                        lawDocs.subList(i * insertNum, (i + 1) * insertNum);
                }
                successNum += remoteLawRetrieveService.insertBatch(subList);
                sendMessage(clientId, successNum, allLawSize);
            }
        }
        return successNum;
    }

//    todo 添加增量同步（查询es所有数据和mysql所有数据，遍历mysql数据借助布隆过滤器判断是否存在于es中，不存在则添加到es：问题在于速度肯定很慢）


    /**
     * 发送同步进度消息
     */
    private void sendMessage(String clientId, int successNum, int allLawSize) {
        WebscoketMessage message = new WebscoketMessage(IdUtil.simpleUUID(), SocketMsgType.LAW.getType(),
            "全量同步法律法规", "同步进度：" + successNum + "/" + allLawSize, clientId);
        remoteWebSocketService.sendToOne(clientId, JSONObject.toJSONString(message));
    }

    /**
     * 修改法律法规
     */
    @Override
    public Boolean updateByBo(LawRegulationBo bo) {
        LawRegulation update = BeanCopyUtils.copy(bo, LawRegulation.class);
        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
//            更新es索引
            flag = remoteLawRetrieveService.update(BeanCopyUtils.copy(update, LawDoc.class)) > 0;
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
            remoteLawRetrieveService.deleteBatch(ids.toArray(new Long[0]));
        }
        return flag;
    }

    /**
     * 批量智能处理法律法规
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    public int process(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<LawRegulation> laws = BeanCopyUtils.copyList(processList, LawRegulation.class);
        if (laws != null) {
            laws.forEach(law -> {
                validEntityBeforeSave(law);
                if (checkRevised(law)) {
                    law.setIsMining(MiningStatus.STRIPED);
                }
                if (checkMininged(law)) {
                    law.setIsMining(MiningStatus.MININGED);
                }
                law.setContent(law.getStripContent());
                boolean sqlFlag = baseMapper.updateById(law) > 0;
                if (sqlFlag) {
                    //            更新es索引
                    boolean esFlag = remoteLawRetrieveService.update(BeanCopyUtils.copy(law, LawDoc.class)) > 0;
                    if (esFlag) {
                        success.addAndGet(1);
                    }
                }
            });
        }
        return success.get();
    }

    /**
     * 批量处理法律法规内容
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    public int processContent(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<LawRegulation> laws = BeanCopyUtils.copyList(processList, LawRegulation.class);
        if (laws != null) {
            laws.forEach(law -> {
                validEntityBeforeSave(law);
                law.setIsMining(MiningStatus.STRIPED);
                boolean sqlFlag = baseMapper.updateById(law) > 0;
                if (sqlFlag) {
                    //            更新es索引
                    boolean esFlag = remoteLawRetrieveService.update(BeanCopyUtils.copy(law, LawDoc.class)) > 0;
                    if (esFlag) {
                        success.addAndGet(1);
                    }
                }
            });
        }
        return success.get();
    }

    /**
     * 批量处理法律法规额外信息
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    public int processExtra(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<LawRegulation> laws = BeanCopyUtils.copyList(processList, LawRegulation.class);
        if (laws != null) {
            laws.forEach(law -> {
                validEntityBeforeSave(law);
                law.setIsMining(MiningStatus.MININGED);
                boolean sqlFlag = baseMapper.updateById(law) > 0;
                if (sqlFlag) {
                    //            更新es索引
                    boolean esFlag = remoteLawRetrieveService.update(BeanCopyUtils.copy(law, LawDoc.class)) > 0;
                    if (esFlag) {
                        success.addAndGet(1);
                    }
                }
            });
        }
        return success.get();
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

    /**
     * 判断是否已经清洗
     *
     * @param law 法条
     * @return boolean
     */
    boolean checkRevised(LawRegulation law) {
        return !StringUtils.isBlank(law.getStripContent()) && !law.getStripContent().equals(law.getContent());
    }

    /**
     * 判断是否已经挖掘
     *
     * @param law 法条
     * @return boolean
     */
    boolean checkMininged(LawRegulation law) {
        String originExtra = "{\"keyword\":\"\",\"field\":\"\",\"type\":\"\",\"organization\":\"\",\"release\":\"\",\"execute\":\"\",\"basis\":[],\"scope\":\"\",\"main\":[],\"abstract\":\"\"}";
        return !StringUtils.isBlank(law.getExtra()) && !law.getExtra().equals(originExtra);
    }
}
