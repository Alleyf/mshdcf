package com.ruoyi.crawler.controller;

import com.ruoyi.crawler.domain.SourceType;
import com.ruoyi.crawler.service.ISourceTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 数据源类型表(SourceType)表控制层
 *
 * @author makejava
 * @since 2024-02-21 20:38:45
 */
@RestController
@RequestMapping("sourceType")
public class SourceTypeController {
    /**
     * 服务对象
     */
    @Resource
    private ISourceTypeService ISourceTypeService;

    /**
     * 分页查询
     *
     * @param sourceType  筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<SourceType>> queryByPage(SourceType sourceType, PageRequest pageRequest) {
        return ResponseEntity.ok(this.ISourceTypeService.queryByPage(sourceType, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<SourceType> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.ISourceTypeService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param sourceType 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<SourceType> add(SourceType sourceType) {
        return ResponseEntity.ok(this.ISourceTypeService.insert(sourceType));
    }

    /**
     * 编辑数据
     *
     * @param sourceType 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<SourceType> edit(SourceType sourceType) {
        return ResponseEntity.ok(this.ISourceTypeService.update(sourceType));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.ISourceTypeService.deleteById(id));
    }

}

