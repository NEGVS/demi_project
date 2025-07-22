package com.project.demo.code.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 测试问题Controller
 */
@RestController
@RequestMapping("/test")
@Tag(name = "测试")
@Slf4j
public class TestController {

    /**
     * 测试大响应 （客户端主动关闭连接）
     * 无法完全避免客户端手动断开连接，后台抛出 Connection reset by peer 异常，但是可以在检测出这个异常的时候
     * 捕获异常，将日志级别调整
     */
    @GetMapping("/large-response")
    public void largeResponse(HttpServletResponse response) throws Exception {
        // 模拟返回一个非常大的响应或慢速写入
        response.setContentType("text/plain");
        for (int i = 0; i < 100000; i++) {
            response.getWriter().write("This is line " + i + "\n");
            response.flushBuffer(); // 刷新输出流，确保客户端能接收到部分内容
            try {
                Thread.sleep(10); // 模拟慢速响应
            } catch (Exception e) {
                log.warn("large-response: {}", e.getMessage());
            }
        }
    }

    @GetMapping("/stream-data")
    public StreamingResponseBody streamData() {
        return outputStream -> {
            try {
                for (int i = 0; i < 100000; i++) {
                    String line = "This is line " + i + "\n";
                    outputStream.write(line.getBytes());
                    outputStream.flush(); // 刷新输出流
                    Thread.sleep(10); // 模拟慢速响应
                }
            } catch (Exception e) {
                // 当客户端断开连接时，可能抛出 IOException
                log.warn("Client disconnected during streaming: {}", e.getMessage());
            }
        };
    }
}
