package com.ruoyi.retrieve.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.api.domain.LawDoc;
import com.ruoyi.retrieve.esmapper.CaseDocMapper;
import com.ruoyi.retrieve.service.ICaseDocService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 司法案例检索
 *
 * @author fcs
 * @date 2024/1/31 16:06
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@RestController
@RequestMapping("/case")
@Validated
@Slf4j
public class CaseDocController extends BaseController {

    @Resource
    private CaseDocMapper caseDocMapper;
    @Resource
    private ICaseDocService caseDocService;

    /**
     * 创建索引
     */
    @GetMapping("/createIndex")
    public R<Void> createIndex() {
        // 1.初始化-> 创建索引(相当于mysql中的表)
        try {
            GetIndexResponse index = caseDocMapper.getIndex();
            if (ObjectUtil.isNotNull(index)) {
                return R.ok("索引已存在");
            }
        } catch (Exception e) {
            return toAjax(caseDocMapper.createIndex());
        }
        return toAjax(caseDocMapper.createIndex());
    }

    /**
     * 删除索引
     *
     * @return R
     */
    @DeleteMapping("/deleteIndex")
    public R<Void> deleteIndex() {
        // 指定要删除哪个索引
        String indexName = CaseDoc.class.getSimpleName().toLowerCase();
        return toAjax(caseDocMapper.deleteIndex(indexName));
    }

    /**
     * 根据id查询
     *
     * @param id id
     * @return R
     */
    @GetMapping("/{id}")
    public R<CaseDoc> get(@PathVariable("id") Long id) {
        return R.ok(caseDocService.selectById(id));
    }

    /**
     * 查询列表
     *
     * @param keyword    关键词
     * @param blurSearch 模糊搜索标志
     * @return R
     */
    @GetMapping("/list")
    public R<List<CaseDoc>> list(String keyword, Boolean blurSearch) {
        return R.ok(caseDocService.selectList(keyword, blurSearch));
    }

    /**
     * 分页查询
     *
     * @param keyword   关键词
     * @param pageQuery 分页参数
     * @return TableDataInfo
     */
    @GetMapping("/pageByKeyword")
    public TableDataInfo<CaseDoc> pageByKeyword(String keyword, PageQuery pageQuery) {
        return caseDocService.selectPage(keyword, pageQuery);
    }

    /**
     * 分页查询
     *
     * @param caseDoc   查询条件
     * @param pageQuery 分页参数
     * @return TableDataInfo
     */
    @GetMapping("/page")
    public TableDataInfo<CaseDoc> page(CaseDoc caseDoc, PageQuery pageQuery) {
        return caseDocService.selectPage(caseDoc, pageQuery);
    }

    /**
     * 新增
     *
     * @param caseDoc 新增对象
     * @return R
     */
    @PostMapping
    public R<Void> insert(@Validated @RequestBody CaseDoc caseDoc) {
        return toAjax(caseDocService.insert(caseDoc));
    }

    /**
     * 修改
     *
     * @param caseDoc 修改对象
     * @return R
     */
    @PutMapping
    public R<Void> edit(@Validated @RequestBody CaseDoc caseDoc) {
        return toAjax(caseDocService.update(caseDoc));
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return R
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        return toAjax(caseDocService.delete(id));
    }

    /**
     * 批量删除
     *
     * @param ids 主键
     * @return R
     */
    @DeleteMapping("/batch/{ids}")
    public R<Void> deleteBatch(@PathVariable Long[] ids) {
        return toAjax(caseDocService.deleteBatch(ids));
    }


}
