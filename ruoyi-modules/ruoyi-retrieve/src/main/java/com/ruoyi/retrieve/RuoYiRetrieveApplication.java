package com.ruoyi.retrieve;

import cn.easyes.starter.register.EsMapperScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * @author fcs
 * @date 2024/1/31 10:46
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 数据检索服务模块
 */
@SpringBootApplication
@EnableDubbo
@EsMapperScan("com.ruoyi.retrieve.esmapper")
public class RuoYiRetrieveApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiRetrieveApplication.class);
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  数据检索服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
