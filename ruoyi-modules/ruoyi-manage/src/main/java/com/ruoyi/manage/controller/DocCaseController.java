package com.ruoyi.manage.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.core.ExcelResult;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.manage.domain.bo.ProcessBo;
import com.ruoyi.manage.domain.vo.DocCaseImportVo;
import com.ruoyi.manage.enums.SocketMsgType;
import com.ruoyi.manage.listener.DocCaseImportListener;
import com.ruoyi.manage.mq.producer.WebsocketProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.bo.DocCaseBo;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 司法案例管理
 *
 * @author alleyf
 * @description 司法案例控制器前端访问路由地址为:/manage/case
 * @date 2024-01-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/case")
public class DocCaseController extends BaseController {

    private final IDocCaseService docCaseService;
    private final WebsocketProducer websocketProducer;

    /**
     * 查询司法案例列表
     */
    @SaCheckPermission("manage:case:list")
    @GetMapping("/list")
    public TableDataInfo<DocCaseVo> list(DocCaseBo bo, PageQuery pageQuery) {
        return docCaseService.queryPageList(bo, pageQuery);
    }


    /**
     * 导入司法案例列表
     *
     * @param file          导入文件
     * @param updateSupport 更新已有数据
     */
    @Log(title = "司法案例", businessType = BusinessType.IMPORT)
    @SaCheckPermission("manage:case:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<DocCaseImportVo> result = ExcelUtil.importExcel(file.getInputStream(), DocCaseImportVo.class, new DocCaseImportListener(updateSupport));
        return R.ok(result.getAnalysis());
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "司法案例数据", DocCaseImportVo.class, response);
    }

    /**
     * 导出司法案例列表
     */
    @SaCheckPermission("manage:case:export")
    @Log(title = "司法案例", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DocCaseBo bo, HttpServletResponse response) {
        List<DocCaseVo> list = docCaseService.queryList(bo);
        ExcelUtil.exportExcel(list, "司法案例", DocCaseVo.class, response);
    }

    /**
     * 导出选中司法案例列表
     */
    @SaCheckPermission("manage:case:export")
    @Log(title = "司法案例", businessType = BusinessType.EXPORT)
    @PostMapping("/exportSelected")
    public void exportIds(Long[] ids, HttpServletResponse response) {
        System.out.println(Arrays.toString(ids));
        System.out.println(ids.getClass());
        List<DocCaseVo> list = docCaseService.queryListByIds(ids);
        ExcelUtil.exportExcel(list, "司法案例", DocCaseVo.class, response);
    }

    /**
     * 获取司法案例详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("manage:case:query")
    @GetMapping("/{id}")
    public R<DocCaseVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(docCaseService.queryById(id));
    }

    /**
     * 新增司法案例
     */
    @SaCheckPermission("manage:case:add")
    @Log(title = "司法案例", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DocCaseBo bo) {
        return toAjax(docCaseService.insertByBo(bo));
    }

    /**
     * 全量同步司法案例
     */
//    @SaCheckPermission("manage:case:add")
//    @Log(title = "司法案例", businessType = BusinessType.INSERT)
    @GetMapping("/syncAll")
    public R<Void> syncAll() {
//        采用消息队列异步处理，借助websocket实时发送处理进度通知
        websocketProducer.sendMsg(SocketMsgType.CASE.getType(), "开始同步司法案例数据", LoginHelper.getLoginUser().getLoginId(), 0L);
//        return R.ok("成功同步司法案例数据：" + docCaseService.insertBatch() + "条");
        return R.ok("开始同步司法案例数据");
    }

    /**
     * 修改司法案例
     */
    @SaCheckPermission("manage:case:edit")
    @Log(title = "司法案例", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DocCaseBo bo) {
        return toAjax(docCaseService.updateByBo(bo));
    }

    /**
     * 批量智能处理司法案例
     *
     * @param processList 待处理案例列表
     */
    @SaCheckPermission("manage:case:edit")
    @Log(title = "司法案例", businessType = BusinessType.UPDATE)
    @PutMapping("/process")
    public R<Void> process(@Validated(EditGroup.class) @RequestBody List<ProcessBo> processList) {
        return R.ok("成功处理：" + docCaseService.process(processList) + "条");
    }

    /**
     * 批量清洗司法案例
     *
     * @param processList 待处理案例列表
     */
    @SaCheckPermission("manage:case:edit")
    @Log(title = "司法案例", businessType = BusinessType.UPDATE)
    @PutMapping("/revise")
    public R<Void> saveContent(@Validated(EditGroup.class) @RequestBody List<ProcessBo> processList) {
        return R.ok("成功处理：" + docCaseService.processContent(processList) + "条");
    }

    /**
     * 批量挖掘司法案例
     *
     * @param processList 待处理案例列表
     */
    @SaCheckPermission("manage:case:edit")
    @Log(title = "司法案例", businessType = BusinessType.UPDATE)
    @PutMapping("/mining")
    public R<Void> saveExtra(@Validated(EditGroup.class) @RequestBody List<ProcessBo> processList) {
        return R.ok("成功处理：" + docCaseService.processExtra(processList) + "条");
    }

    /**
     * 删除司法案例
     *
     * @param ids 主键串
     */
    @SaCheckPermission("manage:case:remove")
    @Log(title = "司法案例", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@Validated @NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(docCaseService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 测试mq-websocket
     *
     * @param msg      消息
     * @param clientId 客户端id
     * @return R<Void>
     */
    @GetMapping("/testmq")
    public R<Void> testmq(String msg, String clientId) {
        websocketProducer.sendMsg(SocketMsgType.NORMAL.getType(), msg, LoginHelper.getLoginUser().getLoginId(), 0L);
        return R.ok("test mq send websocket msg");
    }
}
