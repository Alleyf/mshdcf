package com.ruoyi.manage.controller;

import com.ruoyi.manage.domain.mo.MDocCase;
import com.ruoyi.manage.service.impl.MDocCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 司法案例控制器
 *
 * @author csFan
 */
@RestController
@RequestMapping("/mCase")
@RequiredArgsConstructor
public class MDocCaseController {

    // 注入MDocCaseService服务，用于处理文档案例的业务逻辑
    private final MDocCaseService mDocCaseService;

    /**
     * 根据ID获取文档案例信息。
     *
     * @param id 文档案例的ID，路径变量。
     * @return 返回匹配的MDocCase对象，如果没有找到则返回null。
     */
    @GetMapping("/{id}")
    public MDocCase getDocCaseById(@PathVariable Long id) {
        return mDocCaseService.getDocCaseById(id);
    }

    /**
     * 根据案例名称获取文档案例信息。
     *
     * @param caseName 文档案例的名称，路径变量。
     * @return 返回匹配的MDocCase对象，如果没有找到则返回null。
     */
    @GetMapping("/name/{caseName}")
    public MDocCase getDocCaseByName(@PathVariable String caseName) {
        return mDocCaseService.getDocCaseByName(caseName);
    }

    /**
     * 创建一个新的文档案例。
     *
     * @param mdocCase 包含新文档案例信息的MDocCase对象，请求体中传入。
     * @return 返回保存后的MDocCase对象。
     */
    @PostMapping
    public MDocCase createDocCase(@RequestBody MDocCase mdocCase) {
        return mDocCaseService.saveDocCase(mdocCase);
    }

    /**
     * 根据ID删除文档案例。
     *
     * @param id 要删除的文档案例ID，路径变量。
     */
    @DeleteMapping("/{id}")
    public void deleteDocCase(@PathVariable Long id) {
        mDocCaseService.deleteDocCase(id);
    }
}
