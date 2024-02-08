package com.ruoyi.manage.service.impl;

import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.gpt.GeminiUtils;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import com.ruoyi.manage.utils.ProcessUtils;
import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.RemoteLawDocRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.api.domain.LawDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

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
    @DubboReference(version = "1.0", group = "case", timeout = 200000)
    public RemoteCaseDocRetrieveService remoteCaseDocRetrieveService;
    @DubboReference(version = "1.0", group = "law", timeout = 200000)
    public RemoteLawDocRetrieveService remoteLawDocRetrieveService;


    private int updateCaseEs(DocCase docCase) {
        CaseDoc caseDoc = BeanCopyUtils.copy(docCase, CaseDoc.class);
        return remoteCaseDocRetrieveService.update(caseDoc);
    }

    private int updateLawEs(LawRegulation lawRegulation) {
        LawDoc lawDoc = BeanCopyUtils.copy(lawRegulation, LawDoc.class);
        return remoteLawDocRetrieveService.update(lawDoc);
    }

    /**
     * 案例信息挖掘
     *
     * @param id 案例id
     * @return int 成功数
     */
    public int caseInfoMining(Long id) {
//        首先使用Gemini提示模板进行挖掘，失败则通过设计的规则进行挖掘
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            DocCase docCase = docCaseMapper.selectById(id);
//            AI挖掘
            String extra = GeminiUtils.caseParse(docCase.getContent());
            if (extra == null) {
//                正则挖掘
                extra = ProcessUtils.buildCaseExtra(docCase.getContent());
            }
            docCase.setExtra(extra);
            docCaseMapper.updateById(docCase);
            log.info(String.valueOf(docCase.getExtra().getClass()));
            return updateCaseEs(docCase);
        } else {
            List<DocCase> docCases = docCaseMapper.selectList();
            docCases.forEach(docCase -> {
                String extra = GeminiUtils.caseParse(docCase.getContent());
                if (extra == null) {
                    extra = ProcessUtils.buildCaseExtra(docCase.getContent());
                }
                docCase.setExtra(extra);
                docCaseMapper.updateById(docCase);
                int num = updateCaseEs(docCase);
                if (num > 0) {
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

    /**
     * 法规信息挖掘
     *
     * @param id 法规id
     * @return int 成功数
     */
    public int lawInfoMining(Long id) {
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
            String extra = GeminiUtils.lawParse(lawRegulation.getContent());
            lawRegulation.setExtra(extra);
            lawRegulationMapper.updateById(lawRegulation);
            return updateLawEs(lawRegulation);
        } else {
            List<LawRegulation> lawRegulations = lawRegulationMapper.selectList();
            lawRegulations.forEach(lawRegulation -> {
                String extra = GeminiUtils.lawParse(lawRegulation.getContent());
                lawRegulation.setExtra(extra);
                lawRegulationMapper.updateById(lawRegulation);
                int num = updateLawEs(lawRegulation);
                if (num > 0) {
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

    /**
     * 规范案例正文乱码格式
     *
     * @param id 案例id
     * @return int 成功数
     */
    public int stripCaseContent(Long id) {
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            DocCase docCase = docCaseMapper.selectById(id);
            String stripContent = ProcessUtils.stripCaseUnicode(docCase.getContent());
            docCase.setContent(stripContent);
            docCaseMapper.updateById(docCase);
            return updateCaseEs(docCase);
        } else {
            List<DocCase> docCases = docCaseMapper.selectList();
            docCases.forEach(docCase -> {
                String stripContent = ProcessUtils.stripCaseUnicode(docCase.getContent());
                docCase.setContent(stripContent);
                docCaseMapper.updateById(docCase);
                int num = updateCaseEs(docCase);
                if (num > 0) {
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

    /**
     * 案例正文修正
     *
     * @param id 案例id
     * @return int 成功数
     */
    public int reviseCaseContent(Long id) {
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            DocCase docCase = docCaseMapper.selectById(id);
            String reviseContent = GeminiUtils.caseRevise(docCase.getContent());
            docCase.setContent(reviseContent);
            docCaseMapper.updateById(docCase);
            return updateCaseEs(docCase);
        } else {
            List<DocCase> docCases = docCaseMapper.selectList();
            docCases.forEach(docCase -> {
                String reviseContent = GeminiUtils.caseRevise(docCase.getContent());
                docCase.setContent(reviseContent);
                docCaseMapper.updateById(docCase);
                int num = updateCaseEs(docCase);
                if (num > 0) {
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

    /**
     * 规范法条正文乱码格式
     *
     * @param id 法条id
     * @return 成功数
     */
    public int stripRegulationContent(Long id) {
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
            String stripContent = ProcessUtils.stripLawUnicode(lawRegulation.getContent());
            lawRegulation.setContent(stripContent);
            lawRegulationMapper.updateById(lawRegulation);
            return updateLawEs(lawRegulation);
        } else {
            List<LawRegulation> lawRegulations = lawRegulationMapper.selectList();
            lawRegulations.forEach(lawRegulation -> {
                String stripContent = ProcessUtils.stripLawUnicode(lawRegulation.getContent());
                lawRegulation.setContent(stripContent);
                lawRegulationMapper.updateById(lawRegulation);
                int num = updateLawEs(lawRegulation);
                if (num > 0) {
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

    /**
     * 法条正文修正
     *
     * @param id 法条id
     * @return int 成功数
     */
    public int reviseLawContent(Long id) {
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
            String stripContent = GeminiUtils.lawRevise(lawRegulation.getContent());
            lawRegulation.setContent(stripContent);
            lawRegulationMapper.updateById(lawRegulation);
            return updateLawEs(lawRegulation);
        } else {
            List<LawRegulation> lawRegulations = lawRegulationMapper.selectList();
            lawRegulations.forEach(lawRegulation -> {
                String reviseContent = GeminiUtils.lawRevise(lawRegulation.getContent());
                lawRegulation.setContent(reviseContent);
                lawRegulationMapper.updateById(lawRegulation);
                int num = updateLawEs(lawRegulation);
                if (num > 0) {
                    success.getAndIncrement();
                }
            });
        }
        return success.get();
    }

}
