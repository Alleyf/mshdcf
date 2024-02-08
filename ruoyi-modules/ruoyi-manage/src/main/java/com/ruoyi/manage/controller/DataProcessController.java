package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.manage.service.impl.DataProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 智能处理
 *
 * @author fcs
 * @date 2024/2/4 23:44
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 对司法数据智能处理等<br />
 * <p>生成或抽取正文摘要，法条分类，正文数据清洗、补全、分段（案例：背景，事实，陈述，依据，判决结果；法条：章->节）</p>
 */
//@RequestMapping("/process")
@RestController
@Validated
@Slf4j
public class DataProcessController extends BaseController {

    @Resource
    private DataProcessService dataProcessService;


    /**
     * 清洗所有案例数据
     *
     * @return R<Void>
     */
    @GetMapping("/case/stripAll")
    public R<Void> stripCaseAll() {
        int stripNum = dataProcessService.stripCaseContent(null);
        int reviseNum = dataProcessService.reviseCaseContent(null);
        return R.ok("司法案例数据成功清洗：" + stripNum + "条数据；" + "成功清洗：" + reviseNum + "条。");
    }

    /**
     * 清洗指定案例数据
     *
     * @param id 案例id
     * @return R<Void>
     */
    @GetMapping("/case/strip/{id}")
    public R<Void> stripCaseOne(@PathVariable("id") Long id) {
        boolean stripFlag = dataProcessService.stripCaseContent(id) > 0;
        boolean reviseFlag = dataProcessService.reviseCaseContent(id) > 0;
        return toAjax(stripFlag && reviseFlag);
    }

    /**
     * 清洗所有法条数据
     *
     * @return R<Void>
     */
    @GetMapping("/law/stripAll")
    public R<Void> stripLawAll() {
        int stripNum = dataProcessService.stripRegulationContent(null);
        int reviseNum = dataProcessService.reviseLawContent(null);
        return R.ok("法律法规数据成功格式化：" + stripNum + "条;" + "成功清洗：" + reviseNum + "条。");
    }

    /**
     * 清洗指定法条数据
     *
     * @param id 法条id
     * @return R<Void>
     */
    @GetMapping("/law/strip/{id}")
    public R<Void> stripLawOne(@PathVariable("id") Long id) {
        boolean stripFlag = dataProcessService.stripRegulationContent(id) > 0;
        boolean reviseFlag = dataProcessService.reviseLawContent(id) > 0;
        return toAjax(stripFlag && reviseFlag);
    }

    /**
     * 案例数据挖掘
     *
     * @param id 案例id
     * @return R<Void>
     */
    @GetMapping("/case/extract/{id}")
    public R<Void> caseExtract(@PathVariable("id") Long id) {
        return toAjax(dataProcessService.caseInfoMining(id));
    }

    /**
     * 全部案例数据挖掘
     *
     * @return R<Void>
     */
    @GetMapping("/case/extractAll")
    public R<Void> caseExtractAll() {
        return toAjax(dataProcessService.caseInfoMining(null));
    }

    /**
     * 法条数据挖掘
     *
     * @param id 法条id
     * @return R<Void>
     */
    @GetMapping("/law/extract/{id}")
    public R<Void> lawExtract(@PathVariable("id") Long id) {
        return toAjax(dataProcessService.lawInfoMining(id));
    }

    /**
     * 全部法条数据挖掘
     *
     * @return R<Void>
     */
    @GetMapping("/law/extractAll")
    public R<Void> lawExtractAll() {
        return toAjax(dataProcessService.lawInfoMining(null));
    }

}
