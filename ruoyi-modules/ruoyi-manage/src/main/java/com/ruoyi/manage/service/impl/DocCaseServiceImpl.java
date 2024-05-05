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
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.manage.domain.bo.ProcessBo;
import com.ruoyi.manage.domain.mo.MDocCase;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.enums.MiningStatus;
import com.ruoyi.manage.enums.SocketMsgType;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.websocket.api.RemoteWebSocketService;
import com.ruoyi.websocket.domain.WebscoketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 司法案例Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocCaseServiceImpl extends ServiceImpl<DocCaseMapper, DocCase> implements IDocCaseService {

    private final MDocCaseService mDocCaseService;
    @DubboReference(version = "1.0", group = "case", timeout = 200000)
    private RemoteCaseDocRetrieveService remoteCaseRetrieveService;
    @DubboReference
    private RemoteWebSocketService remoteWebSocketService;
    @Resource
    private DocCaseMapper baseMapper;

    /**
     * 查询司法案例
     */
    @Override
    public DocCaseVo queryById(Long id) {
        DocCaseVo docCaseVo = baseMapper.selectVoById(id);
        // todo: mongodb查询,后续通过exist=false去除mysql在mongo中已有的字段数据
        MDocCase mDocCase = mDocCaseService.getDocCaseById(id);
        fillDocCaseVo(docCaseVo);
        return docCaseVo;
    }

    /**
     * 查询司法案例列表
     */
    @Override
    public TableDataInfo<DocCaseVo> queryPageList(DocCaseBo bo, PageQuery pageQuery) {
//        构建条件查询修饰器
        LambdaQueryWrapper<DocCase> lqw = buildQueryWrapper(bo);
        lqw.eq(ObjectUtil.isNotNull(bo.getIsMining()), DocCase::getIsMining, MiningStatus.getMiningStatus(bo.getIsMining()));
//        传入分页查询器和条件查询修饰器进行分页查询
        Page<DocCaseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        // 填充案例视图对象列表中额外信息
        fillDocCaseVoList(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询文档案例列表
     *
     * @param bo 搜索条件对象，用于构建查询条件
     * @return 返回文档案例的视图对象列表
     */
    @Override
    public List<DocCaseVo> queryList(DocCaseBo bo) {
        // 构建查询条件
        LambdaQueryWrapper<DocCase> lqw = buildQueryWrapper(bo);
        // 根据查询条件从数据库中选择相应的视图列表
        List<DocCaseVo> docCaseVos = baseMapper.selectVoList(lqw);

        // 防御性编程：确保列表不为空且不为null
        if (docCaseVos == null) {
            docCaseVos = new ArrayList<>();
        }

        fillDocCaseVoList(docCaseVos);
        return docCaseVos;
    }

    /**
     * 填充案例视图对象列表中额外信息
     *
     * @param docCaseVos 案例视图对象列表
     * @return 填充后的案例视图对象列表
     */
    private void fillDocCaseVoList(List<DocCaseVo> docCaseVos) {
        // 使用stream而非parallelStream以避免潜在的并发修改异常
        docCaseVos.stream().forEach(docCaseVo -> {
            try {
                // TODO: 实现对MongoDB查询的逻辑优化，以便后续能够通过exist=false去除mysql在mongo中已有的字段数据
                // 则将额外信息填充到视图对象中
                fillDocCaseVo(docCaseVo);
            } catch (Exception e) {
                // 对异常进行适当的处理，例如记录日志等
                // 日志处理逻辑...
                log.error("查询文档案例视图对象时发生异常：{}", e.getMessage());
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        });
    }

    /**
     * 将MongoDB数据填充到DocCaseVo中
     *
     * @param docCaseVo 视图对象
     */
    private void fillDocCaseVo(DocCaseVo docCaseVo) {
        MDocCase mDocCase = mDocCaseService.getDocCaseById(docCaseVo.getId());
        if (mDocCase != null) {
            docCaseVo.setContent(mDocCase.getContent());
            docCaseVo.setStripContent(mDocCase.getStripContent());
            docCaseVo.setRelatedCases(mDocCase.getRelatedCases());
            docCaseVo.setExtra(mDocCase.getExtra());
        }
    }


    /**
     * 查询案例列表
     */
    @Override
    public List<DocCaseVo> queryListByIds(Long[] ids) {
        List<Long> idLs = Arrays.asList(ids);
        List<DocCase> list = baseMapper.selectBatchIds(idLs);
        List<DocCaseVo> docCaseVos = BeanCopyUtils.copyList(list, DocCaseVo.class);
        if (docCaseVos != null) {
            fillDocCaseVoList(docCaseVos);
        }
        return docCaseVos;
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
        lqw.eq(bo.getStatus() != null, DocCase::getStatus, bo.getStatus());
//        todo 不知道为什么，这里的bo.getIsMining()会返回null报错
        lqw.eq(bo.getIsMining() != null, DocCase::getIsMining, MiningStatus.getMiningStatus(bo.getIsMining()));
        return lqw;
    }

    /**
     * 新增司法案例
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(DocCaseBo bo) {
        DocCase add = BeanCopyUtils.copy(bo, DocCase.class);
        if (add != null) {
            validEntityBeforeSave(add);
        }
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            log.info("新增司法案例成功，id：{}", add.getId());
//            DocCaseVo docCaseVo = BeanCopyUtils.copy(add, DocCaseVo.class);
            saveMongoAndEs(add);
        }
        return flag;
    }

    /**
     * 保存司法案例到MongoDB和Elasticsearch
     *
     * @param docCase 司法案例对象，包含需要保存的信息
     */
    @Override
    public void saveMongoAndEs(DocCase docCase) {
        // 添加mongodb记录
        MDocCase mDocCase = BeanCopyUtils.copy(docCase, MDocCase.class);
        if (mDocCase != null && StringUtils.isBlank(mDocCase.getRelatedCases())) {
            mDocCase.setRelatedCases(mDocCaseService.getDocCaseById(docCase.getId()).getRelatedCases());
        }
        MDocCase newMDocCase = mDocCaseService.saveDocCase(mDocCase);
        if (newMDocCase == null) {
            throw new ServiceException("新增司法案例mongodb记录失败");
        }
        CaseDoc caseDoc = BeanCopyUtils.copy(docCase, CaseDoc.class);
        boolean esFlag = remoteCaseRetrieveService.save(caseDoc) > 0;
        if (!esFlag) {
            throw new ServiceException("新增司法案例es索引失败");
        }
    }

    /**
     * 批量添加司法案例到es
     */
    @Override
    public Integer syncAllCaseToEs(String clientId) {
        // 验证clientId的合法性
        Assert.notNull(clientId, "clientId不能为空");

        List<DocCaseVo> allCase = this.queryList(new DocCaseBo());
        // 当查询结果为空时，直接返回0或抛出异常，避免后续逻辑执行
        if (allCase.isEmpty()) {
            return 0;
        }

        List<CaseDoc> caseDocs = BeanCopyUtils.copyList(allCase, CaseDoc.class);
        int allCaseSize = allCase.size();

        // 设置mysqlId
        if (caseDocs != null) {
            caseDocs.forEach(caseDoc -> caseDoc.setMysqlId(caseDoc.getId()));
        }

        int insertNum = 100; // 批量插入数
        int successNum = 0;

        // 分块同步
        if (allCaseSize <= insertNum) {
            successNum = remoteCaseRetrieveService.insertBatch(caseDocs);
            sendMessage(clientId, successNum, allCaseSize);
        } else {
            int epoch = allCaseSize / insertNum + (allCaseSize % insertNum != 0 ? 1 : 0);
            for (int i = 0; i < epoch; i++) {
                List<CaseDoc> subList = null;
                if (caseDocs != null) {
                    subList = (i == epoch - 1) ?
                        caseDocs.subList(i * insertNum, allCaseSize) :
                        caseDocs.subList(i * insertNum, (i + 1) * insertNum);
                }
                successNum += remoteCaseRetrieveService.insertBatch(subList);
                sendMessage(clientId, successNum, allCaseSize);
            }
        }
        return successNum;
    }

//    todo 添加增量同步（查询es所有数据和mysql所有数据，遍历mysql数据借助布隆过滤器判断是否存在于es中，不存在则添加到es：问题在于速度肯定很慢）

    /**
     * 发送同步进度消息
     */
    private void sendMessage(String clientId, int successNum, int allCaseSize) {
        WebscoketMessage message = new WebscoketMessage(IdUtil.simpleUUID(), SocketMsgType.CASE.getType(),
            "全量同步司法案例", "同步进度：" + successNum + "/" + allCaseSize, clientId);
        remoteWebSocketService.sendToOne(clientId, JSONObject.toJSONString(message));
    }

    /**
     * 修改司法案例
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(DocCaseBo bo) {
        DocCase update = BeanCopyUtils.copy(bo, DocCase.class);
        if (update != null) {
            validEntityBeforeSave(update);
        }
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
//            更新mongo记录和es索引
//            DocCaseVo docCaseVo = BeanCopyUtils.copy(update, DocCaseVo.class);
            saveMongoAndEs(update);
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
            // 删除mongodb记录和es索引
            deleteMongoAndEs(ids);
        }
        return flag;
    }

    /**
     * 删除MongoDB和Elasticsearch中的记录
     * 该方法首先会删除指定ID的MongoDB文档，然后删除对应的Elasticsearch索引。
     *
     * @param ids 需要删除的记录的ID集合，类型为Long的Collection
     */
    private void deleteMongoAndEs(Collection<Long> ids) {
        // 删除MongoDB记录
        mDocCaseService.deleteBatch(ids.toArray(new Long[0]));

        // 删除Elasticsearch索引
        remoteCaseRetrieveService.deleteBatch(ids.toArray(new Long[0]));
    }

    /**
     * 批量智能处理司法案例
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int process(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases = BeanCopyUtils.copyList(processList, DocCase.class);
        if (docCases != null) {
            docCases.forEach(docCase -> {
                validEntityBeforeSave(docCase);
                if (checkRevised(docCase)) {
                    docCase.setIsMining(MiningStatus.STRIPED);
                }
                if (checkMininged(docCase)) {
                    docCase.setIsMining(MiningStatus.MININGED);
                }
                docCase.setContent(docCase.getStripContent());
                boolean sqlFlag = baseMapper.updateById(docCase) > 0;
                if (sqlFlag) {
                    // 更新mongo记录和es索引
//                    DocCaseVo docCaseVo = BeanCopyUtils.copy(docCase, DocCaseVo.class);
                    saveMongoAndEs(docCase);
                    success.addAndGet(1);
//                    boolean esFlag = remoteCaseRetrieveService.update(BeanCopyUtils.copy(docCase, CaseDoc.class)) > 0;
                }
            });
        }
        return success.get();
    }

    /**
     * 批量处理司法案例内容
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processContent(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases = BeanCopyUtils.copyList(processList, DocCase.class);
        if (docCases != null) {
            docCases.forEach(docCase -> {
                validEntityBeforeSave(docCase);
                docCase.setIsMining(MiningStatus.STRIPED);
                boolean sqlFlag = baseMapper.updateById(docCase) > 0;
                if (sqlFlag) {
                    // 更新mongo记录和es索引
//                    DocCaseVo docCaseVo = BeanCopyUtils.copy(docCase, DocCaseVo.class);
                    saveMongoAndEs(docCase);
                    success.addAndGet(1);
                }
            });
        }
        return success.get();
    }

    /**
     * 批量处理司法案例额外信息
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processExtra(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases = BeanCopyUtils.copyList(processList, DocCase.class);
        if (docCases != null) {
            docCases.forEach(docCase -> {
                validEntityBeforeSave(docCase);
                docCase.setIsMining(MiningStatus.MININGED);
                boolean sqlFlag = baseMapper.updateById(docCase) > 0;
                if (sqlFlag) {
                    // 更新mongo记录和es索引
                    saveMongoAndEs(docCase);
                    success.addAndGet(1);
//                    boolean esFlag = remoteCaseRetrieveService.update(BeanCopyUtils.copy(docCase, CaseDoc.class)) > 0;
//                    if (esFlag) {
//                        success.addAndGet(1);
//                    }
                }
            });
        }
        return success.get();
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

    /**
     * 判断是否已经清洗
     *
     * @param docCase 案例
     * @return boolean
     */
    boolean checkRevised(DocCase docCase) {
        return !StringUtils.isBlank(docCase.getStripContent()) && !docCase.getStripContent().equals(docCase.getContent());
    }

    /**
     * 判断是否已经挖掘
     *
     * @param docCase 案例
     * @return boolean
     */
    boolean checkMininged(DocCase docCase) {
        String originExtra = "{" + "\"keyword\": \"\"," +
            "    \"summary\": \"\",\n" +
            "    \"plea\": \"\",\n" +
            "    \"label\": \"\",\n" +
            "    \"plai\": \"\",\n" +
            "    \"defe\": \"\",\n" +
            "    \"article\": \"xx\",\n" +
            "    \"party\": {\n" +
            "        \"plaintiff\": \"\",\n" +
            "        \"defendant\": \"\"\n" +
            "    },\n" +
            "    \"fact\": \"\",\n" +
            "    \"note\": \"\"\n" +
            "}";
        return !StringUtils.isBlank(docCase.getExtra()) && !docCase.getExtra().equals(originExtra);
    }
}
