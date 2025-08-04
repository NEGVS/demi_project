package com.project.demo.selenium.zhengZhou;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.code.domain.TradingData;
import com.project.demo.task.AsyncTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author Andy Fan
 * @Date 2025/8/1 16:51
 * @ClassName ZhengZhouService
 */
@Service
@Slf4j
public class ZhengZhouService {
    @Resource
    private RestTemplate restTemplate;

    @Resource
    AsyncTaskService asyncTaskService;
    @Resource
    private ObjectMapper objectMapper;
    private WebDriver driver;

//    @PostConstruct
//    public void init() {
//        // 设置 ChromeDriver 路径（替换为你的实际路径）
//        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
//        // 初始化 WebDriver
//        driver = new ChromeDriver();
//    }

    public String performSearch(String searchTerm) {
        try {
            log.info("--searchTerm: {}", searchTerm);
            // 打开网页
            driver.get("https://www.baidu.com");

            log.info("--打开网页: {}", "https://www.baidu.com");
            // 查找搜索框并输入搜索词
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys(searchTerm);
            searchBox.submit();
            log.info("--sleep-2s打开网页");

            // 等待页面加载（简单示例，建议使用 WebDriverWait）
            Thread.sleep(2000);

            // 返回页面标题
            return driver.getTitle();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    private static final Class<String> HTML = null;

    public void getZhengZhouData00() {
        try {
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36");
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.set("Accept-Language", "zh-CN,zh;q=0.9");
            headers.set("Connection", "keep-alive");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = "http://www.czce.com.cn/cn/DFSStaticFiles/Future/2025/20250801/FutureDataHolding.htm";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // 获取 HTML 内容
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            System.out.println(JSONUtil.toJsonStr(response));

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("HTTP error fetching CZCE data: " + response.getStatusCode());
            }

            String htmlContent = response.getBody();
            if (htmlContent == null || htmlContent.trim().isEmpty()) {
                throw new RuntimeException("Empty HTML response");
            }

//            return parseHtml(htmlContent);
            if (true) {
                return;
            }
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new HttpClientErrorException(response.getStatusCode(), "Failed to fetch data");
            }

//            String htmlContent = response.getBody();
            if (htmlContent == null || htmlContent.trim().isEmpty()) {
                throw new RuntimeException("Empty HTML response");
            }

//            // 解析 HTML
//            List<CommodityData> commodities = parseHtml(htmlContent);
//            logger.info("Parsed {} commodities/contracts", commodities.size());
//
//            // 打印解析结果
//            for (CommodityData commodity : commodities) {
//                logger.info("Commodity/Contract: {}, Date: {}", commodity.name, commodity.date);
//                for (FuturesData data : commodity.rows) {
//                    logger.info("{}", data);
//                }
//            }


            // 获取JSON字符串
            if (ObjectUtil.isEmpty(url)) {
                System.out.println("\nurl jsonResponse is empty");
            }
            String jsonResponse = restTemplate.getForObject(url, HTML);
            System.out.println("\n-----jsonResponse:");
            System.out.println(JSONUtil.toJsonStr(jsonResponse));
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("HTTP error fetching SHFE data: " + e.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching SHFE data: " + e.getMessage());
        }
    }

    public void getZhengZhouData() {
        // 创建固定大小的线程池
        // 获取所有的链接
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 如果需要无头模式
        options.addArguments("--no-sandbox"); // 添加此选项
        options.addArguments("--disable-dev-shm-usage"); // 添加此选项

        options.addArguments("--blink-settings=imagesEnabled=false"); // 禁用图片加载

        WebDriver driver = new ChromeDriver(options);
        String url = "http://www.czce.com.cn/cn/DFSStaticFiles/Future/2025/20250801/FutureDataHolding.htm";
        try {
            log.info("\n\n开始抓取：{}", url);
            asyncTaskService.scrapeZhengZhouDataFromHtm(driver, url);
            log.info("\n\n抓取完成：{}", url);
        } catch (IOException e) {
            log.info("\n\n执行任务异常：{}", e.getMessage());
        }
        driver.quit();
    }


}
