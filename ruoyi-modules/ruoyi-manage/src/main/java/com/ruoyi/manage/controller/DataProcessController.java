package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.manage.service.impl.DataProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 智能处理
 *
 * @author fcs
 * @date 2024/2/4 23:44
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 对司法数据智能处理等<br />
 * <p>生成或抽取正文摘要，法条分类，正文数据清洗、补全、分段（案例：背景，事实，陈述，依据，判决结果；法条：章->节）</p>
 */
//配置gateway网关路由地址前缀为process/**
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
        return R.ok("司法案例数据成功清洗格式化：" + stripNum + "条数据；" + "成功清洗修正：" + reviseNum + "条。");
    }

    /**
     * 清洗批量案例数据
     *
     * @param ids 案例id
     * @return R<Void>
     */
    @PutMapping("/case/strip/{ids}")
    public R<Void> stripCaseBatch(@PathVariable("ids") Long[] ids) {
        log.info(String.format("stripCaseBatch ids:{}", ids));
        System.out.println(Arrays.toString(Arrays.stream(ids).toArray()));
        boolean stripFlag = dataProcessService.stripCaseContent(Arrays.asList(ids)) > 0;
        boolean reviseFlag = dataProcessService.reviseCaseContent(Arrays.asList(ids)) > 0;
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
        return R.ok("法律法规数据成功清洗格式化：" + stripNum + "条;" + "成功清洗修正：" + reviseNum + "条。");
    }

    /**
     * 清洗批量法条数据
     *
     * @param ids 法条id
     * @return R<Void>
     */
    @PutMapping("/law/strip/{ids}")
    public R<Void> stripLawBatch(@PathVariable("ids") Long[] ids) {
        boolean stripFlag = dataProcessService.stripRegulationContent(Arrays.asList(ids)) > 0;
        boolean reviseFlag = dataProcessService.reviseLawContent(Arrays.asList(ids)) > 0;
        return toAjax(stripFlag && reviseFlag);
    }

    /**
     * 挖掘批量案例数据
     *
     * @param ids 案例id
     * @return R<Void>
     */
    @PutMapping("/case/extract/{ids}")
    public R<Void> caseExtract(@PathVariable("ids") Long[] ids) {
        return toAjax(dataProcessService.caseInfoMining(Arrays.asList(ids)));
    }

    /**
     * 挖掘全部案例数据
     *
     * @return R<Void>
     */
    @GetMapping("/case/extractAll")
    public R<Void> caseExtractAll() {
        return toAjax(dataProcessService.caseInfoMining(null));
    }

    /**
     * 挖掘批量法条数据
     *
     * @param ids 法条id
     * @return R<Void>
     */
    @PutMapping("/law/extract/{ids}")
    public R<Void> lawExtract(@PathVariable("ids") Long[] ids) {
        return toAjax(dataProcessService.lawInfoMining(Arrays.asList(ids)));
    }

    /**
     * 挖掘全部法条数据
     *
     * @return R<Void>
     */
    @GetMapping("/law/extractAll")
    public R<Void> lawExtractAll() {
        return toAjax(dataProcessService.lawInfoMining(null));
    }

}
