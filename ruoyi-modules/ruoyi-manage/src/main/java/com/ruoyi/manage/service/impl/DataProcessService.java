package com.ruoyi.manage.service.impl;

import cn.hutool.core.lang.Assert;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.gpt.GeminiUtils;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.enums.MiningStatus;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.manage.utils.ProcessUtils;
import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.RemoteLawDocRetrieveService;
import com.ruoyi.retrieve.api.domain.LawDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fcs
 * @date 2024/1/31 11:47
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DataProcessService {

    private final DocCaseMapper docCaseMapper;
    private final LawRegulationMapper lawRegulationMapper;
    private final IDocCaseService docCaseService;

    //dubbo传输对象类型参数必须实现serialable接口否则会报错
    @DubboReference(version = "1.0", group = "case", timeout = 200000)
    public RemoteCaseDocRetrieveService remoteCaseDocRetrieveService;
    @DubboReference(version = "1.0", group = "law", timeout = 200000)
    public RemoteLawDocRetrieveService remoteLawDocRetrieveService;


    /**
     * 保存(新增和修改)文档案例到MongoDB和Elasticsearch。
     *
     * @param docCase 包含要保存的文档数据的DocCase对象。
     *                该方法通过调用docCaseService的saveMongoAndEs方法，实现将文档数据同时保存到MongoDB和Elasticsearch的功能。
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMongoAndEs(DocCase docCase) {
        docCaseService.saveMongoAndEs(docCase);
    }


    private int updateLawEs(LawRegulation lawRegulation) {
        LawDoc lawDoc = BeanCopyUtils.copy(lawRegulation, LawDoc.class);
        // TODO: 2024/2/11   对象数据必须实现serializable接口且es必须建立索引数据同步后才能更新,Caused by: java.lang.IllegalArgumentException: [Serialization Security] Serialized class org.elasticsearch.client.Response has not implement Serializable interface. Current mode is strict check, will disallow to deserialize it by default.
        return remoteLawDocRetrieveService.update(lawDoc);
    }

    /**
     * 检查索引是否存在
     *
     * @param id id
     * @return boolean
     */
    private boolean existDataCaseIndex(Long id) {
        return remoteCaseDocRetrieveService.exist(id);
    }

    private boolean existDataLawIndex(Long id) {
        return remoteLawDocRetrieveService.exist(id);
    }

    /**
     * 案例信息挖掘
     *
     * @param ids 案例id
     * @return int 成功数
     */
    public int caseInfoMiningSave(List<Long> ids) {
//        首先使用Gemini提示模板进行挖掘，失败则通过设计的规则进行挖掘
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases;
        List<DocCaseVo> docCaseVos;
        if (ids != null) {
            docCaseVos = docCaseService.queryListByIds(ids.toArray(new Long[0]));
//            docCases = docCaseMapper.selectBatchIds(ids);
//            DocCase docCase = docCaseMapper.selectById(id);
////            AI挖掘
//            String extra = GeminiUtils.caseParse(docCase.getContent());
//            if (extra == null) {
////                正则挖掘
//                extra = ProcessUtils.buildCaseExtra(docCase.getContent());
//            }
//            docCase.setExtra(extra);
//            docCase.setIsMining(MiningStatus.MININGED);
//            docCaseMapper.updateById(docCase);
//            log.info(String.valueOf(docCase.getExtra().getClass()));
//            return updateCaseEs(docCase);
        } else {
            docCaseVos = docCaseService.queryList(new DocCaseBo());
//            docCases = docCaseMapper.selectList();
        }
        docCases = BeanCopyUtils.copyList(docCaseVos, DocCase.class);
        if (docCases != null) {
            docCases.stream().forEach(docCase -> {
//            清洗过的数据才能进行挖掘
                if (docCase.getIsMining() == MiningStatus.STRIPED && existDataCaseIndex(docCase.getId())) {
                    String extra = GeminiUtils.caseParse(docCase.getStripContent());
                    if (extra == null) {
//                从原文or修正文？中提取信息
                        extra = ProcessUtils.buildCaseExtra(docCase.getStripContent());
                    }
                    docCase.setExtra(extra);
                    docCase.setIsMining(MiningStatus.MININGED);
                    docCaseMapper.updateById(docCase);
                    saveMongoAndEs(docCase);
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

    /**
     * 案例信息挖掘
     *
     * @param id 案例id
     * @return int 挖掘后的案例
     */
    public DocCase caseInfoMining(Long id) {
//        首先使用Gemini提示模板进行挖掘，失败则通过设计的规则进行挖掘
        DocCaseVo docCaseVo = docCaseService.queryById(id);
        DocCase docCase = BeanCopyUtils.copy(docCaseVo, DocCase.class);
//        DocCase docCase = docCaseMapper.selectById(id);
        if (docCase != null) {
//            清洗过的数据才能进行挖掘
            String extra = GeminiUtils.caseParse(docCase.getStripContent());
            if (extra == null) {
//                从原文or修正文？中提取信息
                extra = ProcessUtils.buildCaseExtra(docCase.getStripContent());
            }
            docCase.setExtra(extra);
        }
        return docCase;
    }

    /**
     * 法规信息挖掘
     *
     * @param ids 法规id
     * @return int 成功数
     */
    public int lawInfoMiningSave(List<Long> ids) {
        AtomicInteger success = new AtomicInteger(0);
        List<LawRegulation> lawRegulations;
        if (ids != null) {
            lawRegulations = lawRegulationMapper.selectBatchIds(ids);
//            LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
//            String extra = GeminiUtils.lawParse(lawRegulation.getContent());
//            lawRegulation.setExtra(extra);
//            lawRegulation.setIsMining(MiningStatus.MININGED);
//            lawRegulationMapper.updateById(lawRegulation);
//            return updateLawEs(lawRegulation);
        } else {
            lawRegulations = lawRegulationMapper.selectList();
        }
        lawRegulations.forEach(lawRegulation -> {
            //            清洗过的数据才能进行挖掘
            if (lawRegulation.getIsMining() == MiningStatus.STRIPED && existDataLawIndex(lawRegulation.getId())) {
                String extra = GeminiUtils.lawParse(lawRegulation.getStripContent());
                lawRegulation.setExtra(extra);
                lawRegulation.setIsMining(MiningStatus.MININGED);
                lawRegulationMapper.updateById(lawRegulation);
                int num = updateLawEs(lawRegulation);
                if (num > 0) {
                    success.getAndIncrement();
                }
            }
        });
        return success.get();
    }

    /**
     * 法规信息挖掘
     *
     * @param id 法规id
     * @return int 挖掘后的法规
     */
    public LawRegulation lawInfoMining(Long id) {
//        首先使用Gemini提示模板进行挖掘，失败则通过设计的规则进行挖掘
        LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
        Assert.notNull(lawRegulation, "id为：" + id + "的数据不存在");
//            清洗过的数据才能进行挖掘
        String extra = GeminiUtils.lawParse(lawRegulation.getStripContent());
        lawRegulation.setExtra(extra);
        return lawRegulation;
    }

    /**
     * 规范案例正文乱码格式
     *
     * @param ids 案例id
     * @return int 成功数
     */
    public int stripCaseContent(List<Long> ids) {
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases;
        List<DocCaseVo> docCaseVos;
        if (ids != null) {
            docCaseVos = docCaseService.queryListByIds(ids.toArray(new Long[0]));
//            docCases = docCaseMapper.selectBatchIds(ids);
        } else {
            docCaseVos = docCaseService.queryList(new DocCaseBo());
//            docCases = docCaseMapper.selectList();
        }
        docCases = BeanCopyUtils.copyList(docCaseVos, DocCase.class);
        if (docCases != null) {
            docCases.forEach(docCase -> {
                if (existDataCaseIndex(docCase.getId())) {
                    String stripContent = ProcessUtils.stripCaseUnicode(docCase.getContent());
                    docCase.setStripContent(stripContent);
                    docCaseMapper.updateById(docCase);
                    saveMongoAndEs(docCase);
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

    /**
     * 批量案例正文修正
     *
     * @param ids 案例id
     * @return int 成功数
     */
    public int reviseCaseContentSave(List<Long> ids) {
        AtomicInteger success = new AtomicInteger(0);
        List<DocCase> docCases;
        List<DocCaseVo> docCaseVos;
        if (ids != null) {
            docCaseVos = docCaseService.queryListByIds(ids.toArray(new Long[0]));
//            docCases = docCaseMapper.selectBatchIds(ids);
        } else {
            docCaseVos = docCaseService.queryList(new DocCaseBo());
//            docCases = docCaseMapper.selectList();
        }
        docCases = BeanCopyUtils.copyList(docCaseVos, DocCase.class);

        if (docCases != null) {
            docCases.forEach(docCase -> {
                // 原始数据才需要清洗
                if (docCase.getIsMining() == MiningStatus.ORIGIN && existDataCaseIndex(docCase.getId())) {
                    String reviseContent = GeminiUtils.caseRevise(docCase.getStripContent());
                    docCase.setStripContent(reviseContent);
                    docCase.setIsMining(MiningStatus.STRIPED);
                    docCaseMapper.updateById(docCase);
                    saveMongoAndEs(docCase);
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }


    /**
     * 指定id的案例正文修正不保存
     *
     * @param id 案例id
     * @return 修正后的案例
     */
    public DocCase reviseCaseContent(Long id) {
        DocCaseVo docCaseVo = docCaseService.queryById(id);
//        DocCase docCase = docCaseMapper.selectById(id);
        DocCase docCase = BeanCopyUtils.copy(docCaseVo, DocCase.class);
//        Assert.notNull(docCase, "id为：" + id + "的数据不存在");
        //            原始数据才需要清洗
        if (docCase != null) {
            String content = ProcessUtils.stripCaseUnicode(docCase.getContent());
            content = GeminiUtils.caseRevise(content);
            docCase.setStripContent(content);
        }
        return docCase;
    }

    /**
     * 规范法条正文乱码格式
     *
     * @param ids 法条id
     * @return 成功数
     */
    public int stripRegulationContent(List<Long> ids) {
        AtomicInteger success = new AtomicInteger(0);
        List<LawRegulation> lawRegulations;
        if (ids != null) {
            lawRegulations = lawRegulationMapper.selectBatchIds(ids);
//            LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
//            String stripContent = ProcessUtils.stripLawUnicode(lawRegulation.getContent());
//            lawRegulation.setContent(stripContent);
//            lawRegulationMapper.updateById(lawRegulation);
//            return updateLawEs(lawRegulation);
        } else {
            lawRegulations = lawRegulationMapper.selectList();
        }
        lawRegulations.forEach(lawRegulation -> {
            if (existDataLawIndex(lawRegulation.getId())) {
                String stripContent = ProcessUtils.stripLawUnicode(lawRegulation.getContent());
                lawRegulation.setStripContent(stripContent);
                lawRegulationMapper.updateById(lawRegulation);
                int num = updateLawEs(lawRegulation);
                if (num > 0) {
                    success.getAndIncrement();
                }
            }
        });
        return success.get();
    }

    /**
     * 批量法条正文修正
     *
     * @param ids 法条id
     * @return int 成功数
     */
    public int reviseLawContentSave(List<Long> ids) {
        AtomicInteger success = new AtomicInteger(0);
        List<LawRegulation> lawRegulations;
        if (ids != null) {
            lawRegulations = lawRegulationMapper.selectBatchIds(ids);
//            LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
//            String stripContent = GeminiUtils.lawRevise(lawRegulation.getContent());
//            lawRegulation.setContent(stripContent);
//            lawRegulation.setIsMining(MiningStatus.STRIPED);
//            lawRegulationMapper.updateById(lawRegulation);
//            return updateLawEs(lawRegulation);
        } else {
            lawRegulations = lawRegulationMapper.selectList();
        }
        lawRegulations.forEach(lawRegulation -> {
            //            原始数据才需要清洗,es中存在该数据才可以更新
            if (lawRegulation.getIsMining() == MiningStatus.ORIGIN && existDataLawIndex(lawRegulation.getId())) {
                String reviseContent = GeminiUtils.lawRevise(lawRegulation.getStripContent());
                lawRegulation.setStripContent(reviseContent);
                lawRegulation.setIsMining(MiningStatus.STRIPED);
                lawRegulationMapper.updateById(lawRegulation);
                int num = updateLawEs(lawRegulation);
                if (num > 0) {
                    success.getAndIncrement();
                }
            }
        });
        return success.get();
    }

    /**
     * 指定id的法条正文修正不保存
     *
     * @param id 法条id
     * @return 修正后的法条
     */
    public LawRegulation reviseLawContent(Long id) {
        LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
        Assert.notNull(lawRegulation, "id为：" + id + "的数据不存在");
        //            原始数据才需要清洗
        String content = ProcessUtils.stripCaseUnicode(lawRegulation.getContent());
        content = GeminiUtils.lawRevise(content);
        lawRegulation.setStripContent(content);
        return lawRegulation;
    }
}
