package com.project.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableAsync
// 定时任务
@EnableScheduling
@Slf4j
public class DemoApplication {

    public static String tempPort = "7860";
    public static String tempIp = "127.0.0.1";

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext run = SpringApplication.run(DemoApplication.class, args);
        ConfigurableEnvironment env = run.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("Swagger文档 :http://{}/doc.html", ip + ":" + port);

        tempPort = port;
        tempIp = ip;
    }

    //   解决方案
//以下是修复 RestTemplate Bean 缺失的步骤：
//1. 在主配置类中定义 RestTemplate Bean
//在 DemoApplication 或其他配置类中添加 RestTemplate 的 Bean 定义：
// @Bean 注解告诉 Spring 在启动时创建一个 RestTemplate 实例，并将其注册到容器中。
//这将满足 ZhengZhouService 的依赖注入需求。
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
