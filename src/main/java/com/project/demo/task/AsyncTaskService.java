package com.project.demo.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.demo.code.domain.TradingData;
import com.project.demo.code.mapper.TradingDataMapper;
import com.project.demo.common.util.MyUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取tradingData数据使用
 */
@Service
@Slf4j
public class AsyncTaskService {
    private static final int BATCH_SIZE = 1000;
    @Resource
    private TradingDataMapper tradingDataMapper;

    @Resource
    private JavaMailSender mailSender;

    /**
     * 1- Async executeAsyncTaskV2
     */
    @Async
    public void executeAsyncTaskV2(List<String> dateList, String mail) {
        long start = System.currentTimeMillis();
        log.info("异步任务入参：{}, mail:{}", dateList, mail);
        for (String s : dateList) {
            LambdaQueryWrapper<TradingData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TradingData::getDate, MyUtil.getDateyyyy_MM_dd(s));
            int delete = tradingDataMapper.delete(queryWrapper);
            if (delete > 0) {
                log.info(s + "数据清除成功");
            } else {
                log.info(s + "数据清除失败");
            }
        }


//        getDateyyyy_MM_dd

        graspingDataV4(dateList);
        System.out.println("异步任务执行完毕，耗时：" + (System.currentTimeMillis() - start) + "毫秒");
        try {
            if (StrUtil.isNotBlank(mail)) {
                String join = CollUtil.join(dateList, ",");
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("hylogan@qq.com");
                message.setTo(mail);
                message.setSubject("同步任务");
                message.setText(join + "任务同步完成");
                mailSender.send(message);
                message.setTo("1933525074");
                mailSender.send(message);
            }
        } catch (Exception e) {
            log.info("发送邮件错误");
        }
    }


    /**
     * 批量插入
     */
    private static List<List<TradingData>> splitTradingDataList(List<TradingData> list) {
        List<List<TradingData>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += AsyncTaskService.BATCH_SIZE) {
            partitions.add(list.subList(i, Math.min(i + AsyncTaskService.BATCH_SIZE, list.size())));
        }
        return partitions;
    }

    private static void getRowData(WebElement webElement, String[] result, List<String> result1, List<List<String>> result2) {
        List<WebElement> cells = webElement.findElements(By.tagName("td"));
        List<String> rowData = new ArrayList<>();
        int i = 0;
        for (WebElement cell : cells) {
            if (i == 0) {
                i++;
                continue;
            }
            rowData.add(cell.getText());
        }
        rowData.add(result[0]);
        rowData.add(result1.get(0));
        rowData.add(result[1]);
        result2.add(rowData);
    }

    public static void addTradingData(List<TradingData> insertList, List<String> datum, TradingData tradingData) {
        tradingData.setLongPosition(Integer.valueOf(datum.get(1)));
        tradingData.setChangeNum(Integer.valueOf(datum.get(2)));
        tradingData.setNetLongShort(Integer.valueOf(datum.get(3)));
        tradingData.setDate(DateUtil.parse(datum.get(4)));
        tradingData.setCommodity(datum.get(5));
        tradingData.setType(datum.get(6));
        insertList.add(tradingData);
    }

    @NotNull
    private static Result getResult(WebDriver driver) {
        List<WebElement> table_buy = driver.findElements(By.cssSelector("body #table_buy > tbody > tr"));
        List<WebElement> table_sale = driver.findElements(By.cssSelector("body  #table_sale > tbody > tr"));
        WebElement table_buy_title = driver.findElement(By.cssSelector("body > div.type_table > div > div:nth-child(1) > div.chart_title"));
        WebElement table_sale_title = driver.findElement(By.cssSelector("body > div.type_table > div > div:nth-child(2) > div.chart_title"));
        String tableBuyTitleText = table_buy_title.getText();
        List<String> table_buy_title_list = Arrays.stream(tableBuyTitleText.split(" ")).toList();
        String tableBuyTile = table_buy_title_list.get(1);
        String[] tableBuyTileArray = tableBuyTile.split("\n");

        String tableSaleTitleText = table_sale_title.getText();
        List<String> table_sale_title_list = Arrays.stream(tableSaleTitleText.split(" ")).toList();
        String tableSaleTile = table_sale_title_list.get(1);
        String[] tableSaleTileArray = tableSaleTile.split("\n");
        List<List<String>> data = new ArrayList<>();
        return new Result(table_buy, table_sale, table_buy_title_list, tableBuyTileArray, table_sale_title_list, tableSaleTileArray, data);
    }

    static void getTableSale(List<WebElement> table_sale, List<String> table_sale_title_list, String[] tableSaleTileArray, List<List<String>> data) {
        for (WebElement webElement : table_sale) {
            getRowData(webElement, tableSaleTileArray, table_sale_title_list, data);
        }
    }

    /**
     * 获取链接信息
     */
    private List<String> fetchLinks(WebDriver driver) {
        driver.get("https://cc.17kqh.com/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        log.info("获取链接信息开始");
        List<WebElement> webElements = driver.findElements(By.cssSelector("body > div.container > div:nth-child(3) > div > div > div > ul > li > a"));
        List<String> list = new ArrayList<>();
        for (WebElement webElement : webElements) {
            list.add(webElement.getAttribute("href"));
        }
        log.info("获取链接信息结束");
        return list;
    }

    /**
     * 2-根据日期 获取链接
     */
    public void graspingDataV4(List<String> dateList) {
        // 创建固定大小的线程池
        // 获取所有的链接
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 如果需要无头模式
        options.addArguments("--no-sandbox"); // 添加此选项
        options.addArguments("--disable-dev-shm-usage"); // 添加此选项

        options.addArguments("--blink-settings=imagesEnabled=false"); // 禁用图片加载

        WebDriver driver = new ChromeDriver(options);
        List<String> list = fetchLinks(driver);
        log.info("\n\n一共有{}个链接", list.size());
        List<TradingData> insertList = new ArrayList<>();
        for (String s : list) {
            for (String month : dateList) {
                String url = s + "/" + month;
                try {
                    log.info("\n\n开始抓取：{}", url);
                    scrapeDataFromPage2(driver, url, insertList);
                } catch (IOException e) {
                    log.info("\n\n执行任务异常：{}", e.getMessage());
                }
            }
        }
        driver.quit();

        // 创建线程池
        log.info("=========开始插入数据");
        ExecutorService executorService = Executors.newFixedThreadPool(10 * 2);
        List<List<TradingData>> tradingDataList = splitTradingDataList(insertList);
        for (List<TradingData> tradingList : tradingDataList) {
            executorService.submit(() -> {
                log.info("\n\n----插入数据：{}条", tradingList.size());
                // 模拟数据库提交操作
                tradingDataMapper.insertList(tradingList);
            });
        }
        log.info("插入数据完成,总共 = " + insertList.size());
        // 关闭线程池
        executorService.shutdown();
    }

    /**
     * 3-抓取数据
     */
    private void scrapeDataFromPage2(WebDriver driver, String url, List<TradingData> insertList) throws IOException {
        driver.get(url);
        // 找到对应的数据地址
        WebElement display = driver.findElement(By.cssSelector("body > div.desktop > div > div:nth-child(1) > div"));
        // 检测是否以表格形式打开
        String style = display.getAttribute("style");
        if (!style.contains("display: none")) {
            WebElement button = driver.findElement(By.cssSelector("#switch"));
            // 模拟点击事件（切换表格显示）
            button.click();
            // 等待10秒页面刷新
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.type_table > div > div")));
        }
        // 查找特定元素下的表格
        Result result = getResult(driver);
        // 使用 LambdaQueryWrapper 构造删除条件
//        LambdaQueryWrapper<TradingData> queryWrapper = new LambdaQueryWrapper<>();
//        // 将传入的日期的数据全部删除，重新插入
//        queryWrapper.eq(TradingData::getDate, result.tableBuyTileArray()[0]).eq(TradingData::getCommodity, result.table_buy_title_list().get(0));
//        if (tradingDataMapper.selectCount(queryWrapper) > 0) {
//            tradingDataMapper.delete(queryWrapper);
//        }
        // 多单
        for (WebElement webElement : result.table_buy()) {
            if (webElement.getAttribute("class").contains("opacity05")) {
                continue;
            }
            getRowData(webElement, result.tableBuyTileArray(), result.table_buy_title_list(), result.data());
        }
        // 空单
        getTableSale(result.table_sale(), result.table_sale_title_list(), result.tableSaleTileArray(), result.data());
        // 组装数据
        AtomicInteger index = new AtomicInteger(0);
        for (List<String> datum : result.data()) {
            if (datum.size() == 7) {
                log.info("开始组装数据--" + index.getAndIncrement());
                TradingData tradingData = new TradingData();
                tradingData.setSeat(datum.get(0));
                try {
                    addTradingData(insertList, datum, tradingData);
                } catch (NumberFormatException e) {
                    System.out.println("详情异常" + e.getMessage());
                }
            }
        }
    }

    private record Result(List<WebElement> table_buy, List<WebElement> table_sale, List<String> table_buy_title_list,
                          String[] tableBuyTileArray, List<String> table_sale_title_list, String[] tableSaleTileArray,
                          List<List<String>> data) {
    }

    /**
     * 3-抓取数据
     */
    public void scrapeZhengZhouDataFromHtm(WebDriver driver, String url) throws IOException {
        driver.get(url);
        try {
            // 等待页面加载
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // 等待目标元素可见
            WebElement display = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("body > div.desktop > div > div:nth-child(1) > div")));

            // 检查 style 属性
            String style = display.getAttribute("style");
            if (!style.contains("display: none")) {
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#switch")));
                button.click();

                // 等待表格显示
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.type_table > div > div")));
            }
            // 查找特定元素下的表格
            AsyncTaskService.Result result = getResult(driver);
            // 多单
            for (WebElement webElement : result.table_buy()) {
                if (webElement.getAttribute("class").contains("opacity05")) {
                    continue;
                }
                getRowData(webElement, result.tableBuyTileArray(), result.table_buy_title_list(), result.data());
            }
            // 空单
            getTableSale(result.table_sale(), result.table_sale_title_list(), result.tableSaleTileArray(), result.data());
            // 组装数据
            AtomicInteger index = new AtomicInteger(0);
            for (List<String> datum : result.data()) {
                if (datum.size() == 7) {
                    log.info("开始组装数据--" + index.getAndIncrement());
                    try {
                    } catch (NumberFormatException e) {
                        System.out.println("详情异常" + e.getMessage());
                    }
                }
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            log.error("无法找到元素: {}", e.getMessage());
            throw new IOException("页面元素未找到", e);
        } catch (org.openqa.selenium.TimeoutException e) {
            log.error("等待元素超时: {}", e.getMessage());
            throw new IOException("页面加载超时", e);
        }
    }
}
