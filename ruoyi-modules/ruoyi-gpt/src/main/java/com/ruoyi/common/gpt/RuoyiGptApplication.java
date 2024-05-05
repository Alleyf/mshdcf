package com.ruoyi.common.gpt;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @date 2024/3/14 12:19
 */
@SpringBootApplication
//@EnableDubbo
public class RuoyiGptApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RuoyiGptApplication.class);
        springApplication.run(args);
        System.out.println("AI服务启动成功");
    }
}
