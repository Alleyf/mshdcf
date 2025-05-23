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
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.manage.domain.bo.ProcessBo;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.enums.MiningStatus;
import com.ruoyi.manage.enums.SocketMsgType;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.websocket.api.RemoteWebSocketService;
import com.ruoyi.websocket.domain.WebscoketMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 司法案件Service业务层处理
 *
 * @author alleyf
 * @date 2024-01-26
 */
@Service
@Slf4j
public class DocCaseServiceImpl extends ServiceImpl<DocCaseMapper, DocCase> implements IDocCaseService {

    @DubboReference(version = "1.0", group = "case", timeout = 200000)
    private RemoteCaseDocRetrieveService remoteCaseRetrieveService;
    @DubboReference
    private RemoteWebSocketService remoteWebSocketService;
    @Resource
    private DocCaseMapper baseMapper;

    /**
     * 查询司法案件
     */
    @Override
    public DocCaseVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询司法案件列表
     */
    @Override
    public TableDataInfo<DocCaseVo> queryPageList(DocCaseBo bo, PageQuery pageQuery) {
//        构建条件查询修饰器
        LambdaQueryWrapper<DocCase> lqw = buildQueryWrapper(bo);
        lqw.eq(ObjectUtil.isNotNull(bo.getIsMining()), DocCase::getIsMining, MiningStatus.getMiningStatus(bo.getIsMining()));
//        传入分页查询器和条件查询修饰器进行分页查询
        Page<DocCaseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询司法案件列表
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
        lqw.eq(bo.getStatus() != null, DocCase::getStatus, bo.getStatus());
        lqw.eq(bo.getIsMining() != null, DocCase::getIsMining, MiningStatus.getMiningStatus(bo.getIsMining()));
        lqw.orderByDesc(DocCase::getCreateTime);
        return lqw;
    }

    /**
     * 新增司法案件
     */
    @Override
    public Boolean insertByBo(DocCaseBo bo) {
        DocCase add = BeanUtil.toBean(bo, DocCase.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            log.info("新增司法案件成功，id：{}", add.getId());
//            bo.setId(add.getId());
//            添加es索引
            DocCase newCase = selectDocCaseByName(add.getName());
            flag = remoteCaseRetrieveService.insert(BeanCopyUtils.copy(newCase, CaseDoc.class)) > 0;
        }
        return flag;
    }

    /**
     * 全量同步司法案件到es
     *
     * @param clientId 客户端ID
     * @return {@link Integer }
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncAll(String clientId) {
        // 验证clientId的合法性
        Assert.notNull(clientId, "clientId不能为空");
        List<DocCase> allCase = baseMapper.selectList();
        Integer successNum = syncCase(clientId, allCase);
        return successNum == null ? 0 : successNum;
    }

    /**
     * 增量同步司法案件到es
     *
     * @param clientId 客户端ID
     * @return {@link Integer }
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncInc(String clientId) {
        Assert.notNull(clientId, "clientId不能为空");
        List<CaseDoc> caseDocs = remoteCaseRetrieveService.selectList();
        // 创建一个包含caseDocs中所有ID的Set
        Set<Long> caseIds = caseDocs.parallelStream()
            .map(CaseDoc::getId)
            .collect(Collectors.toSet());
        if (caseDocs.isEmpty()) {
            syncAll(clientId);
        }
        // 过滤出allCase中ID不在caseIds中的DocCase对象
        List<DocCase> IncCases = baseMapper.selectList(Wrappers.<DocCase>lambdaQuery().notIn(DocCase::getId, caseIds));
        // 增量同步数据
        Integer successNum = syncCase(clientId, IncCases);
        successNum = successNum == null ? 0 : successNum;
        sendMessage(clientId, successNum, IncCases.size());
        return successNum;
    }


    /**
     * 同步司法案件
     *
     * @param clientId 客户端ID
     * @param allCase  待同步的案件
     * @return {@link Integer }
     */
    private Integer syncCase(String clientId, List<DocCase> allCase) {
        // 当查询结果为空时，直接返回0或抛出异常，避免后续逻辑执行
        if (allCase.isEmpty()) {
            return null;
        }

        List<CaseDoc> caseDocs = BeanCopyUtils.copyList(allCase, CaseDoc.class);
        int allCaseSize = allCase.size();

        // 设置mysqlId
//        caseDocs.parallelStream().forEach(caseDoc -> caseDoc.setMysqlId(caseDoc.getId()));

        int insertNum = 300; // 批量插入数
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

    /**
     * 发送同步进度消息
     */
    private void sendMessage(String clientId, int successNum, int allCaseSize) {
        WebscoketMessage message = new WebscoketMessage(IdUtil.simpleUUID(), SocketMsgType.CASE.getType(),
            "同步司法案件", "同步进度：" + successNum + "/" + allCaseSize, clientId);
        remoteWebSocketService.sendToOne(clientId, JSONObject.toJSONString(message));
    }

    /**
     * 修改司法案件
     */
    @Override
    public Boolean updateByBo(DocCaseBo bo) {
        DocCase update = BeanUtil.toBean(bo, DocCase.class);
        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
//            更新es索引
            Boolean exist = remoteCaseRetrieveService.exist(update.getId());
            if (!exist) {
                flag = remoteCaseRetrieveService.insert(BeanCopyUtils.copy(update, CaseDoc.class)) > 0;
            } else {
                flag = remoteCaseRetrieveService.update(BeanCopyUtils.copy(update, CaseDoc.class)) > 0;
            }
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
     * 批量删除司法案件
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
     * 批量智能处理司法案件
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
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
                    //            更新es索引
                    boolean esFlag = remoteCaseRetrieveService.update(BeanCopyUtils.copy(docCase, CaseDoc.class)) > 0;
                    if (esFlag) {
                        success.addAndGet(1);
                    }
                }
            });
        }
        return success.get();
    }

    /**
     * 批量处理司法案件内容
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    public int processContent(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases = BeanCopyUtils.copyList(processList, DocCase.class);
        if (docCases != null) {
            docCases.forEach(docCase -> {
                validEntityBeforeSave(docCase);
                docCase.setIsMining(MiningStatus.STRIPED);
                boolean sqlFlag = baseMapper.updateById(docCase) > 0;
                if (sqlFlag) {
                    //            更新es索引
                    boolean esFlag = remoteCaseRetrieveService.update(BeanCopyUtils.copy(docCase, CaseDoc.class)) > 0;
                    if (esFlag) {
                        success.addAndGet(1);
                    }
                }
            });
        }
        return success.get();
    }

    /**
     * 批量处理司法案件额外信息
     *
     * @param processList 待处理列表
     * @return int 成功个数
     */
    @Override
    public int processExtra(List<ProcessBo> processList) {
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases = BeanCopyUtils.copyList(processList, DocCase.class);
        if (docCases != null) {
            docCases.forEach(docCase -> {
                validEntityBeforeSave(docCase);
                docCase.setIsMining(MiningStatus.MININGED);
                boolean sqlFlag = baseMapper.updateById(docCase) > 0;
                if (sqlFlag) {
                    //            更新es索引
                    boolean esFlag = remoteCaseRetrieveService.update(BeanCopyUtils.copy(docCase, CaseDoc.class)) > 0;
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
