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
 * @description 对司法数据进行统计分析，数据处理等<br/>
 * todo <p>1. 司法案例和法律法规数据总量</p>
 *  <p>2. 司法案例和法律法规数据量增量（模糊查询新建日期大于等于前一天的所有数据量）</p>
 *  <p>3. 全国各省案件数量，各文书类型数量，各根案由数量，各审判程序数量，各个数据来源数量</p>
 *  <p>4. 生成或抽取正文摘要，法条分类，正文数据清洗、补全、分段（案例：背景，事实，陈述，依据，判决结果；法条：章->节）</p>
 */
@RequestMapping("/process")
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
        int success = dataProcessService.stripCaseContent(null);
        return R.ok("司法案例数据成功清洗：" + success + "条数据。");
    }

    /**
     * 清洗指定案例数据
     *
     * @param id 案例id
     * @return R<Void>
     */
    @GetMapping("/case/strip/{id}")
    public R<Void> stripCaseOne(@PathVariable("id") Long id) {
        return toAjax(dataProcessService.stripCaseContent(id));
    }

    /**
     * 清洗所有法条数据
     *
     * @return R<Void>
     */
    @GetMapping("/law/stripAll")
    public R<Void> stripLawAll() {
        int success = dataProcessService.stripRegulationContent(null);
        return R.ok("法律法规数据成功清洗：" + success + "条数据。");
    }

    /**
     * 清洗指定法条数据
     *
     * @param id 法条id
     * @return R<Void>
     */
    @GetMapping("/law/strip/{id}")
    public R<Void> stripLawOne(@PathVariable("id") Long id) {
        return toAjax(dataProcessService.stripRegulationContent(id));
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
     * 案例数据挖掘
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
     * 法条数据挖掘
     *
     * @return R<Void>
     */
    @GetMapping("/law/extractAll")
    public R<Void> lawExtractAll() {
        return toAjax(dataProcessService.lawInfoMining(null));
    }

}
