package com.ruoyi.crawler.manager;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crawler.domain.bo.CrawlerNodeBo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author fcs
 * @date 2024/1/23 14:55
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@FeignClient(url = "${crawlab.url}", name = "crawlabUrl")
public interface CrawlabManager {


    /**
     * 获取节点信息
     *
     * @param authentication token
     * @param id             节点id
     * @return 返回结果
     */
    @GetMapping(value = "/api/nodes/{id}")
    JSONObject getNode(@RequestHeader(value = "Authorization", defaultValue = "${crawlab.token}") String authentication,
                       @PathVariable("id") String id);

    /**
     * 获取节点列表
     *
     * @param authentication token
     * @param page           页码
     * @param pageSize       每页数量
     * @return 返回结果
     */
    @RequestMapping(value = "/api/nodes", method = RequestMethod.GET)
    JSONObject listNodes(@RequestHeader(value = "Authorization", defaultValue = "${crawlab.token}") String authentication,
                         @RequestParam(value = "page", defaultValue = "1") Integer page,
                         @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize);

    /**
     * 添加节点
     *
     * @param authentication token
     * @param node           节点信息
     * @return 返回结果
     */
    @RequestMapping(value = "/api/nodes/{id}", method = RequestMethod.PUT)
    JSONObject updateNode(@RequestHeader(value = "Authorization", defaultValue = "${crawlab.token}") String authentication,
                          @PathVariable("id") String id,
                          @RequestBody CrawlerNodeBo node);

    /**
     * 删除节点
     *
     * @param authentication token
     * @param ids            节点id
     * @return 返回结果
     */
    @RequestMapping(value = "/api/nodes", method = RequestMethod.DELETE)
    JSONObject deleteNodes(@RequestHeader(value = "Authorization", defaultValue = "${crawlab.token}") String authentication,
                           @RequestBody String[] ids);
}
