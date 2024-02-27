package com.ruoyi.manage;

import com.alibaba.csp.sentinel.adapter.dubbo3.config.DubboAdapterGlobalConfig;
import com.ruoyi.common.websocket.websocket.WebSocketService;
import com.ruoyi.manage.dubbo.RetrieveDubboFallback;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author fcs
 * @date 2024/1/23 14:50
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@SpringBootApplication
@EnableDubbo
public class RuoYiManageApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiManageApplication.class);
//        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        ConfigurableApplicationContext applicationContext = application.run(args);
        DubboAdapterGlobalConfig.setConsumerFallback(new RetrieveDubboFallback());
        System.out.println("(♥◠‿◠)ﾉﾞ  分析管理服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
