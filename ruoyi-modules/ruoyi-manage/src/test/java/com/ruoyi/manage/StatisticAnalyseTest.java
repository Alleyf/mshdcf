package com.ruoyi.manage;

import com.ruoyi.manage.service.impl.StatisticAnalyseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author fcs
 * @date 2024/2/8 13:09
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@SpringBootTest
public class StatisticAnalyseTest {

    @Resource
    private StatisticAnalyseService statisticAnalyseService;

    @Test
    public void testCountCasesByProvince() {
        System.out.println(statisticAnalyseService.countCasesByProvince());
    }

    @Test
    public void testCaseTotal() {
        System.out.println(statisticAnalyseService.caseTotal());
    }

    @Test
    public void testCaseIncrement() {
        System.out.println(statisticAnalyseService.caseIncrement());
    }

    @Test
    public void testLawTotal() {
        System.out.println(statisticAnalyseService.lawTotal());
    }

    @Test
    public void testLawIncrement() {
        System.out.println(statisticAnalyseService.lawIncrement());
    }

    @Test
    public void testCountCasesByType() {
        System.out.println(statisticAnalyseService.countCasesByType());
    }

    @Test
    public void testCountCasesByRootCase() {
        System.out.println(statisticAnalyseService.countCasesByRootCase());
    }

    @Test
    public void testCountCasesByProcess() {
        System.out.println(statisticAnalyseService.countCasesByProcess());
    }

    @Test
    public void testCountLawsByType() {
        System.out.println(statisticAnalyseService.countLawsByType());
    }

}
