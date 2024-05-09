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
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.manage.domain.bo.LawRegulationBo;
import com.ruoyi.manage.domain.bo.ProcessBo;
import com.ruoyi.manage.domain.vo.LawRegulationImportVo;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.enums.SocketMsgType;
import com.ruoyi.manage.listener.LawRegulationImportListener;
import com.ruoyi.manage.mq.producer.WebsocketProducer;
import com.ruoyi.manage.service.ILawRegulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 法律法规管理
 *
 * @author alleyf
 * @description 前端访问路由地址为:/manage/regulation
 * @date 2024-01-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/regulation")
public class LawRegulationController extends BaseController {

    private final ILawRegulationService lawRegulationService;
    private final WebsocketProducer websocketProducer;

    /**
     * 查询法律法规列表
     */
    @SaCheckPermission("manage:regulation:list")
    @GetMapping("/list")
    public TableDataInfo<LawRegulationVo> list(LawRegulationBo bo, PageQuery pageQuery) {
        return lawRegulationService.queryPageList(bo, pageQuery);
    }

    /**
     * 导入法律法规列表
     *
     * @param file          导入文件
     * @param updateSupport 更新已有数据
     */
    @Log(title = "法律法规", businessType = BusinessType.IMPORT)
    @SaCheckPermission("manage:regulation:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<LawRegulationImportVo> result = ExcelUtil.importExcel(file.getInputStream(), LawRegulationImportVo.class, new LawRegulationImportListener(updateSupport));
        return R.ok(result.getAnalysis());
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "法律法规数据", LawRegulationImportVo.class, response);
    }


    /**
     * 导出法律法规列表
     */
    @SaCheckPermission("manage:regulation:export")
    @Log(title = "法律法规", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(LawRegulationBo bo, HttpServletResponse response) {
        List<LawRegulationVo> list = lawRegulationService.queryList(bo);
        ExcelUtil.exportExcel(list, "法律法规", LawRegulationVo.class, response);
    }

    /**
     * 获取法律法规详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("manage:regulation:query")
    @GetMapping("/{id}")
    public R<LawRegulationVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(lawRegulationService.queryById(id));
    }

    /**
     * 新增法律法规
     */
    @SaCheckPermission("manage:regulation:add")
    @Log(title = "法律法规", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LawRegulationBo bo) {
        return toAjax(lawRegulationService.insertByBo(bo));
    }

    /**
     * 全量同步法律法规
     */
//    @SaCheckPermission("manage:regulation:add")
//    @Log(title = "法律法规", businessType = BusinessType.INSERT)
    @GetMapping("/syncAll")
    public R<Void> syncAll() {
        //        采用消息队列异步处理，借助websocket实时发送处理进度通知
        websocketProducer.sendMsg("全量同步法条数据", SocketMsgType.LAW.getType(), "开始同步司法法条数据", LoginHelper.getLoginId(), 0L);
        return R.ok("开始同步法条数据");
    }

    /**
     * 修改法律法规
     */
    @SaCheckPermission("manage:regulation:edit")
    @Log(title = "法律法规", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LawRegulationBo bo) {
        return toAjax(lawRegulationService.updateByBo(bo));
    }

    /**
     * 批量智能处理法律法规
     *
     * @param processList 待处理法条列表
     */
    @SaCheckPermission("manage:case:edit")
    @Log(title = "法律法规", businessType = BusinessType.UPDATE)
    @PutMapping("/process")
    public R<Void> process(@Validated(EditGroup.class) @RequestBody List<ProcessBo> processList) {
        return R.ok("成功处理：" + lawRegulationService.process(processList) + "条");
    }

    /**
     * 批量清洗法律法规
     *
     * @param processList 待处理法条列表
     */
    @SaCheckPermission("manage:case:edit")
    @Log(title = "法律法规", businessType = BusinessType.UPDATE)
    @PutMapping("/revise")
    public R<Void> saveContent(@Validated(EditGroup.class) @RequestBody List<ProcessBo> processList) {
        return R.ok("成功处理：" + lawRegulationService.processContent(processList) + "条");
    }

    /**
     * 批量挖掘法律法规
     *
     * @param processList 待处理法条列表
     */
    @SaCheckPermission("manage:case:edit")
    @Log(title = "法律法规", businessType = BusinessType.UPDATE)
    @PutMapping("/mining")
    public R<Void> saveExtra(@Validated(EditGroup.class) @RequestBody List<ProcessBo> processList) {
        return R.ok("成功处理：" + lawRegulationService.processExtra(processList) + "条");
    }

    /**
     * 删除法律法规
     *
     * @param ids 主键串
     */
    @SaCheckPermission("manage:regulation:remove")
    @Log(title = "法律法规", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(lawRegulationService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
