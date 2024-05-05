package com.ruoyi.manage;

import com.alibaba.csp.sentinel.adapter.dubbo3.config.DubboAdapterGlobalConfig;
import com.ruoyi.manage.dubbo.RetrieveDubboFallback;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
        ConfigurableApplicationContext applicationContext = application.run(args);
        DubboAdapterGlobalConfig.setConsumerFallback(new RetrieveDubboFallback());
        System.out.println("(♥◠‿◠)ﾉﾞ  分析管理服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
