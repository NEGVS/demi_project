package com.project.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */
@Configuration
@Slf4j
public class ThreadPoolConfig {

    @Bean(name = "executorService")
    public ExecutorService executorService() {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        log.info("可用处理器（内核）: " + cpuCount);
        return new ThreadPoolExecutor(cpuCount, // 核心线程数
                cpuCount * 2, // 最大线程数
                30L, // 空闲线程等待时间
                TimeUnit.SECONDS, // 时间单位
                new ArrayBlockingQueue<>(500), // 任务队列，容量为300
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
    }
}
