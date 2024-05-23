package com.ruoyi.manage.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.service.impl.DataProcessService;
import com.ruoyi.websocket.api.RemoteWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference
    private RemoteWebSocketService remoteWebSocketService;


    /**
     * 清洗所有案例数据
     *
     * @return R<Void>
     */
    @GetMapping("/case/stripAll")
    public R<Void> stripCaseAll() {
        int stripNum = dataProcessService.stripCaseContent(null);
        int reviseNum = dataProcessService.reviseCaseContentSave(null);
        return R.ok("司法案件数据成功清洗格式化：" + stripNum + "条数据；" + "成功清洗修正：" + reviseNum + "条。");
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
        boolean reviseFlag = dataProcessService.reviseCaseContentSave(Arrays.asList(ids)) > 0;
        return toAjax(stripFlag && reviseFlag);
    }

    /**
     * 清洗单个案例数据
     *
     * @param id 案例id
     * @return R<DocCase>
     */
    @GetMapping("/case/revise/{id}")
    public R<DocCase> reviseCase(@PathVariable("id") Long id) {
        DocCase docCase = dataProcessService.reviseCaseContent(id);
        return R.ok(docCase);
    }

    /**
     * 清洗所有法条数据
     *
     * @return R<Void>
     */
    @GetMapping("/law/stripAll")
    public R<Void> stripLawAll() {
        int stripNum = dataProcessService.stripRegulationContent(null);
        int reviseNum = dataProcessService.reviseLawContentSave(null);
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
        boolean reviseFlag = dataProcessService.reviseLawContentSave(Arrays.asList(ids)) > 0;
        return toAjax(stripFlag && reviseFlag);
    }

    /**
     * 清洗单个法条数据
     *
     * @param id 法条id
     * @return R<LawRegulation>
     */
    @GetMapping("/law/revise/{id}")
    public R<LawRegulation> reviseLaw(@PathVariable("id") Long id) {
        LawRegulation lawRegulation = dataProcessService.reviseLawContent(id);
        return R.ok(lawRegulation);
    }

    /**
     * 挖掘批量案例数据
     *
     * @param ids 案例id
     * @return R<Void>
     */
    @PutMapping("/case/extract/{ids}")
    public R<Void> caseExtract(@PathVariable("ids") Long[] ids) {
        return toAjax(dataProcessService.caseInfoMiningSave(Arrays.asList(ids)));
    }

    /**
     * 挖掘单个案例数据
     *
     * @param id 案例id
     * @return R<DocCase>
     */
    @GetMapping("/case/extractOne/{id}")
    public R<DocCase> caseExtract(@PathVariable("id") Long id) {
        DocCase docCase = dataProcessService.caseInfoMining(id);
        return R.ok(docCase);
    }

    /**
     * 挖掘全部案例数据
     *
     * @return R<Void>
     */
    @GetMapping("/case/extractAll")
    public R<Void> caseExtractAll() {
        return toAjax(dataProcessService.caseInfoMiningSave(null));
    }

    /**
     * 挖掘批量法条数据
     *
     * @param ids 法条id
     * @return R<Void>
     */
    @PutMapping("/law/extract/{ids}")
    public R<Void> lawExtract(@PathVariable("ids") Long[] ids) {
        return toAjax(dataProcessService.lawInfoMiningSave(Arrays.asList(ids)));
    }

    /**
     * 挖掘单个法条数据
     *
     * @param id 法条id
     * @return R<LawRegulation>
     */
    @GetMapping("/law/extractOne/{id}")
    public R<LawRegulation> lawExtract(@PathVariable("id") Long id) {
        LawRegulation lawRegulation = dataProcessService.lawInfoMining(id);
        return R.ok(lawRegulation);
    }

    /**
     * 挖掘全部法条数据
     *
     * @return R<Void>
     */
    @GetMapping("/law/extractAll")
    public R<Void> lawExtractAll() {
        return toAjax(dataProcessService.lawInfoMiningSave(null));
    }

    /**
     * 推送消息
     *
     * @param clientId 客户端id
     * @param message  消息内容
     * @return R<Void>
     */
    @GetMapping("/push")
    public R<Void> push(@RequestParam(value = "clientId", required = false) String clientId, @RequestParam("message") String message) {
        if (null != clientId) {
            remoteWebSocketService.sendToAll(message);
        } else {
            remoteWebSocketService.sendToOne(clientId, message);
        }
        return R.ok();
    }
}
