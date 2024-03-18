package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 监控管理
 * Redis信息监控
 *
 * @author csFan
 * @version 1.0
 * @description redis信息监控
 * @date 2024/3/18 11:18
 */
@Slf4j
@RestController
@RequestMapping("/monitor/cache")
public class SysRedisController extends BaseController {

    /**
     * 获取缓存系统信息
     *
     * <p>该接口无需参数，通过调用 {@link RedisUtils#getInfo()} 方法，
     * 从Redis中获取相关信息后，封装成响应体返回。</p>
     *
     * @return R<Map < String, Object>> 返回一个包含系统信息的响应体。响应体是一个Map对象，其中键值对表示不同的系统信息项。
     */
    @GetMapping("info")
    public R<Map<String, Object>> info() {
        // 调用RedisUtils中的方法获取Redis信息，并将获取的信息封装成成功响应体返回
        return R.ok(RedisUtils.getInfo());
    }

}
