package com.ruoyi.retrieve.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.retrieve.domain.vo.LawRegulationVo;
import com.ruoyi.retrieve.domain.bo.LawRegulationBo;
import com.ruoyi.retrieve.service.ILawRegulationService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 法律法规控制器
 * 前端访问路由地址为:/retrieve/regulation
 *
 * @author alleyf
 * @date 2024-01-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/regulation")
public class LawRegulationController extends BaseController {

    private final ILawRegulationService iLawRegulationService;

    /**
     * 查询法律法规列表
     */
    @SaCheckPermission("retrieve:regulation:list")
    @GetMapping("/list")
    public TableDataInfo<LawRegulationVo> list(LawRegulationBo bo, PageQuery pageQuery) {
        return iLawRegulationService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出法律法规列表
     */
    @SaCheckPermission("retrieve:regulation:export")
    @Log(title = "法律法规", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(LawRegulationBo bo, HttpServletResponse response) {
        List<LawRegulationVo> list = iLawRegulationService.queryList(bo);
        ExcelUtil.exportExcel(list, "法律法规", LawRegulationVo.class, response);
    }

    /**
     * 获取法律法规详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("retrieve:regulation:query")
    @GetMapping("/{id}")
    public R<LawRegulationVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(iLawRegulationService.queryById(id));
    }

    /**
     * 新增法律法规
     */
    @SaCheckPermission("retrieve:regulation:add")
    @Log(title = "法律法规", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LawRegulationBo bo) {
        return toAjax(iLawRegulationService.insertByBo(bo));
    }

    /**
     * 修改法律法规
     */
    @SaCheckPermission("retrieve:regulation:edit")
    @Log(title = "法律法规", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LawRegulationBo bo) {
        return toAjax(iLawRegulationService.updateByBo(bo));
    }

    /**
     * 删除法律法规
     *
     * @param ids 主键串
     */
    @SaCheckPermission("retrieve:regulation:remove")
    @Log(title = "法律法规", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iLawRegulationService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
