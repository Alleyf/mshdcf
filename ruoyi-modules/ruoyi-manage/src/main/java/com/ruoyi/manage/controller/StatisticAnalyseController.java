package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.service.impl.DataProcessService;
import com.ruoyi.manage.service.impl.StatisticAnalyseService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 统计分析
 *
 * @author fcs
 * @date 2024/2/8 12:23
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 对平台数据进行综合统计分析，进行可视化展示
 */
//@RequestMapping("/statistic")
@RestController
@Validated
@Slf4j
public class StatisticAnalyseController extends BaseController {

    @Resource
    private StatisticAnalyseService statisticAnalyseService;


    /**
     * 获取案件总数
     *
     * @return R<Long> 案件总数
     */
    @GetMapping("/case/total")
    public R<Long> caseTotal() {
        return R.ok(statisticAnalyseService.caseTotal());
    }

    /**
     * 获取案件日增量
     *
     * @return R<Long> 案件日增量
     */
    @GetMapping("/case/increment")
    public R<Long> caseIncrement() {
        return R.ok(statisticAnalyseService.caseIncrement());
    }

    /**
     * 获取法律法规总数
     *
     * @return R<Long> 法律法规总数
     */
    @GetMapping("/law/total")
    public R<Long> lawTotal() {
        return R.ok(statisticAnalyseService.lawTotal());
    }

    /**
     * 获取法律法规日增量
     *
     * @return R<Long> 法律法规日增量
     */
    @GetMapping("/law/increment")
    public R<Long> lawIncrement() {
        return R.ok(statisticAnalyseService.lawIncrement());
    }

    /**
     * 获取案件按省份统计
     *
     * @return R<Map < String, Integer>> 案件按省份统计
     */
    @GetMapping("/case/count/province")
    public R<Map<String, Integer>> countCasesByProvince() {
        return R.ok(statisticAnalyseService.countCasesByProvince());
    }

    /**
     * 获取案件按类型统计
     *
     * @return R<Map < String, Integer>> 案件按类型统计
     */
    @GetMapping("/case/count/type")
    public R<Map<String, Long>> countCasesByType() {
        return R.ok(statisticAnalyseService.countCasesByType());
    }

    /**
     * 获取案件按审判进程统计
     *
     * @return R<Map < String, Integer>> 案件按审判进程统计
     */
    @GetMapping("/case/count/process")
    public R<Map<String, Integer>> countCasesByProcess() {
        return R.ok(statisticAnalyseService.countCasesByProcess());
    }

    /**
     * 获取案件按案由统计
     *
     * @return R<Map < String, Integer>> 案件按案由统计
     */
    @GetMapping("/case/count/cause")
    public R<Map<String, Integer>> countCasesByRootCause() {
        return R.ok(statisticAnalyseService.countCasesByRootCase());
    }

    /**
     * 获取法律法规按类型统计
     *
     * @return R<Map < String, Integer>> 法律法规按类型统计
     */
    @GetMapping("/law/count/type")
    public R<Map<String, Long>> countLawsByType() {
        return R.ok(statisticAnalyseService.countLawsByType());
    }


    /**
     * 获取最新十条案件
     *
     * @return R<List < DocCaseVo>> 最新十条案件
     */
    @GetMapping("/case/rankTen")
    public R<List<DocCaseVo>> selectNewTenCases() {
        return R.ok(statisticAnalyseService.selectNewTenCases());
    }

    /**
     * 获取最新十条法律法规
     *
     * @return R<List < LawRegulationVo>> 最新十条法律法规
     */
    @GetMapping("/law/rankTen")
    public R<List<LawRegulationVo>> selectNewTenLaws() {
        return R.ok(statisticAnalyseService.selectNewTenLaws());
    }
}
