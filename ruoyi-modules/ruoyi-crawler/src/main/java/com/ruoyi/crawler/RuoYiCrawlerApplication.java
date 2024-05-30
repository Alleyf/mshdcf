package com.ruoyi.crawler;

import com.tangzc.autotable.springboot.EnableAutoTable;
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
// 代表的含义就是，本地、开发、测试环境 开启自动建表功能
@EnableAutoTable
public class RuoYiCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiCrawlerApplication.class);
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  采集管理模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
