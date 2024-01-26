package com.ruoyi.crawler.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.crawler.crawlab.CrawlabManager;
import com.ruoyi.crawler.domain.vo.CrawlerNode;
import com.ruoyi.crawler.domain.vo.CrawlerNodeVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.json.Json;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fcs
 * @date 2024/1/23 15:20
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@RestController
@RequestMapping("/crawler")
public class NodeController {

    /**
     * crawlab的token
     */
    @Value("${crawlab.token}")
    private String token;
    @Resource
    private CrawlabManager crawlabManager;

    /**
     * 获取节点列表
     *
     * @param page     页码
     * @param pageSize 页大小
     * @return 返回节点列表
     */
    @GetMapping("/node/list")
    public TableDataInfo<CrawlerNode> list(@RequestParam(value = "page") Integer page,
                                           @RequestParam(value = "page_size") Integer pageSize) {
        JSONObject res = crawlabManager.listNodes(token, page, pageSize);
        List<CrawlerNode> nodeLs = CrawlerNode.map(res.getJSONArray("data"));
        TableDataInfo<CrawlerNode> nodeTable = new TableDataInfo<>(nodeLs, res.getLong("total"));
        nodeTable.setMsg(res.getString("message"));
        return nodeTable;
    }

    /**
     * 获取节点详情
     *
     * @param id 节点id
     * @return 返回节点详情
     */
    @GetMapping("/node/{id}")
    public R<CrawlerNode> get(@PathVariable("id") String id) {
        JSONObject res = crawlabManager.getNode(token, id);
        CrawlerNode node = CrawlerNode.map(res.getJSONObject("data"));
        return R.ok(node);
    }

    /**
     * 更新节点
     *
     * @param id   节点id
     * @param node 节点信息
     * @return 返回更新结果
     */
    @PutMapping("/node/update/{id}")
    public R<String> update(@PathVariable("id") String id,
                            @RequestBody CrawlerNodeVo node) {
        JSONObject res = crawlabManager.updateNode(token, id, node);
        return R.ok(res.getString("message"));
    }

    /**
     * 删除节点
     *
     * @param ids 节点id
     * @return 返回删除结果
     */
    @DeleteMapping("/node/delete")
    public R<String> delete(@RequestBody String[] ids) {
        JSONObject res = crawlabManager.deleteNodes(token, ids);
        return R.ok(res.getString("message"));
    }

}
