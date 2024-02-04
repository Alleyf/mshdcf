package com.ruoyi.manage.dubbo;

import com.alibaba.csp.sentinel.adapter.dubbo3.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ruoyi.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;

/**
 * @author fcs
 * @date 2024/2/4 13:52
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Slf4j
public class RetrieveDubboFallback implements DubboFallback {
    @Override
    public Result handle(Invoker<?> invoker, Invocation invocation, BlockException e) {
        log.error("检索服务器忙, 参数：{}, 服务名：{}, 方法：{}", invocation.getArguments(), invocation.getServiceName(), invocation.getMethodName());
        throw new ServiceException("检索服务器忙，请稍后再试");
    }
}
