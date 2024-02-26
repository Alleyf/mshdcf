package com.ruoyi.websocket.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fcs
 * @date 2024/2/26 18:57
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
//@EnableDubbo
@SpringBootApplication
public class RuoyiWebSocketApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoyiWebSocketApplication.class);
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  WebSocket模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
