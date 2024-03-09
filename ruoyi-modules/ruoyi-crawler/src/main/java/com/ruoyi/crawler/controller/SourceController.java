package com.ruoyi.crawler.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.crawler.domain.SourceType;
import com.ruoyi.crawler.service.ISourceTypeService;
import com.ruoyi.crawler.utils.GeneratorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.crawler.domain.vo.SourceVo;
import com.ruoyi.crawler.domain.bo.SourceBo;
import com.ruoyi.crawler.service.ISourceService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据源管理
 *
 * @author alleyf
 * @description 前端访问路由地址为:/crawlerdata/source
 * @date 2024-01-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/source")
public class SourceController extends BaseController {

    private final ISourceService sourceService;


    /**
     * 查询爬虫数据源列表
     */
    @SaCheckPermission("crawlerdata:source:list")
    @GetMapping("/list")
    public TableDataInfo<SourceVo> list(SourceBo bo, PageQuery pageQuery) {
        return sourceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出爬虫数据源列表
     */
    @SaCheckPermission("crawlerdata:source:export")
    @Log(title = "爬虫数据源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SourceBo bo, HttpServletResponse response) {
        List<SourceVo> list = sourceService.queryList(bo);
        ExcelUtil.exportExcel(list, "爬虫数据源", SourceVo.class, response);
    }

    /**
     * 获取爬虫数据源详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("crawlerdata:source:query")
    @GetMapping("/{id}")
    public R<SourceVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(sourceService.queryById(id));
    }

    /**
     * 新增爬虫数据源
     */
    @SaCheckPermission("crawlerdata:source:add")
    @Log(title = "爬虫数据源", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SourceBo bo) {
        return toAjax(sourceService.insertByBo(bo));
    }

    /**
     * 修改爬虫数据源
     */
    @SaCheckPermission("crawlerdata:source:edit")
    @Log(title = "爬虫数据源", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SourceBo bo) {
        return toAjax(sourceService.updateByBo(bo));
    }

    /**
     * 删除爬虫数据源
     *
     * @param ids 主键串
     */
    @SaCheckPermission("crawlerdata:source:remove")
    @Log(title = "爬虫数据源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(sourceService.deleteWithValidByIds(Arrays.asList(ids), true));
    }


    /**
     * 下载爬虫模板
     *
     * @param id 数据源id
     * @return ResponseEntity<byte [ ]> 爬虫解析模板压缩包
     */
    @PostMapping("/spiderTemplate")
    @SaCheckPermission("crawlerdata:source:export")
    public ResponseEntity<byte[]> downloadTemplate(@RequestParam Long id) throws IOException {
        byte[] zipBytes = sourceService.generateCode(id);
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=SpiderParseTemplate.zip");
        headers.add("Content-Type", "application/zip");
        // 返回压缩包
        return ResponseEntity.ok()
            .headers(headers)
            .body(zipBytes);
    }
}
