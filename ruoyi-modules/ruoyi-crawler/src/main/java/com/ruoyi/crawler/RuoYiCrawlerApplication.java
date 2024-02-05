package com.ruoyi.crawler;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author fcs
 * @date 2024/1/23 14:50
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@SpringBootApplication
@EnableFeignClients
@EnableDubbo
public class RuoYiCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiCrawlerApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  采集管理模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
