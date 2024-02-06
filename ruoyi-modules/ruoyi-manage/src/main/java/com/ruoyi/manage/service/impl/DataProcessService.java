package com.ruoyi.manage.service.impl;

import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.gpt.GeminiUtils;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.RemoteLawDocRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.api.domain.LawDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
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
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            DocCase docCase = docCaseMapper.selectById(id);
            String extra = GeminiUtils.caseParse(docCase.getContent());
            docCase.setExtra(extra);
            docCaseMapper.updateById(docCase);
            log.info(String.valueOf(docCase.getExtra().getClass()));
            return updateCaseEs(docCase);
        } else {
            List<DocCase> docCases = docCaseMapper.selectList();
            docCases.forEach(docCase -> {
                String extra = GeminiUtils.caseParse(docCase.getContent());
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
            String stripContent = StringUtils.stripCaseUnicode(docCase.getContent());
            docCase.setContent(stripContent);
            docCaseMapper.updateById(docCase);
            return updateCaseEs(docCase);
        } else {
            List<DocCase> docCases = docCaseMapper.selectList();
            docCases.forEach(docCase -> {
                String stripContent = StringUtils.stripCaseUnicode(docCase.getContent());
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
     * 规范法条正文乱码格式
     *
     * @param id 法条id
     * @return 成功数
     */
    public int stripRegulationContent(Long id) {
        AtomicInteger success = new AtomicInteger(0);
        if (id != null) {
            LawRegulation lawRegulation = lawRegulationMapper.selectById(id);
            String stripContent = StringUtils.stripLawUnicode(lawRegulation.getContent());
            lawRegulation.setContent(stripContent);
            lawRegulationMapper.updateById(lawRegulation);
            return updateLawEs(lawRegulation);
        } else {
            List<LawRegulation> lawRegulations = lawRegulationMapper.selectList();
            lawRegulations.forEach(lawRegulation -> {
                String stripContent = StringUtils.stripLawUnicode(lawRegulation.getContent());
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

    @Test
    public void test() {
        String s = StringUtils.stripCaseUnicode("申请执行人：隆某某1被执行人：蒙某某1被的行人蒙某某1罚金一案，申请人于2023年3月6日移送执行，要求被执行人交纳罚金120000元，本院于2023年3月6日立案执行。\uE464本案由杨成作任审判员，李学任法官助理，胡乾德任书记员。本院于2023年3月6日立案执行依法向被执行人送达执行通知书，财产报告令、合议庭成员通知书，违法风险告知书。\uE249本案在执行过程中，已扣划了被执行人存款1006元，主办人员依法通过网络查询，到被执行人实地了解调查，但未发现被执行人有可供执行的财产，申请执行人认为被执行人还在服刑，暂时无可供执行的财产，同意终结执行。\uE3B0依照《中华人民共和国民事诉讼法》第二百五十七条第（六）项的规定，裁定如下：终结（2023）桂1031执386号的执行本裁定送达后即发生法律效力。审判员杨成作二〇二三年三月二十七日法官助理李学书记员胡乾德");
        System.out.println(s);
        System.out.println(StringUtils.buildJsonArray(s));
        System.out.println(StringUtils.buildJsonObj(s));
    }

}
