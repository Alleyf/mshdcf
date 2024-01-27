package com.ruoyi.retrieve;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * @author fcs
 * @date 2024/1/23 14:50
 * @site <a href="https://alleyf.github.io">getHelp</a>
 */
@SpringBootApplication
@EnableDubbo
public class RuoYiRetrieveApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiRetrieveApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  检索服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
