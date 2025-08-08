package com.project.demo.code.selenium.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.project.demo.code.domain.TradingData;
import com.project.demo.code.mapper.TradingDataMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在用
 * 抓取数据 郑州、广州
 */
@Service
@Slf4j
public class AsyncTaskServiceB {
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 30;
    private static final int AWAIT_TERMINATION_SECONDS = 30;
    private static final int BATCH_SIZE = 1000;
    private static final int MAX_RETRIES = 3;
    @Resource
    private TradingDataMapper tradingDataMapper;

    @Resource
    private JavaMailSender mailSender;

    /**
     * 1-抓取数据
     *
     * @param dateList 日期集合
     * @param mail     邮箱
     */
    @Async
    public void executeAsyncTaskV_B(List<String> dateList, String mail) throws IOException {
        try {
            long start = System.currentTimeMillis();
            log.info("异步任务入参：{},mail {}", dateList, mail);
            if (ObjectUtil.isEmpty(dateList)) {
                dateList.add("20250801");
                log.info("异步任务入参为空，使用默认参数: 20250801");
            }
            if (mail == null) {
                mail = "andyfaupassion@gmail.com";
                log.info("异步任务入参为空，使用默认参数 mail: andyfaupassion@gmail.com");
            }


            // 获取所有的链接
            long startTime = System.currentTimeMillis();
            // 设置 ChromeDriver 路径（根据实际路径调整）
//            System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless"); // 如果需要无头模式，禁用 Headless 模式：//某些网站对 headless 浏览器有检测，尝试禁用 --headless 测试

            options.addArguments("--disable-extensions");
            options.addArguments("--start-maximized");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.7204.184 Safari/537.36");

            options.addArguments("--no-sandbox"); // 添加此选项
            options.addArguments("--disable-dev-shm-usage"); // 添加此选项
            options.addArguments("--disable-gpu");
//处理 Cookies 或登录：
//检查是否需要登录或设置 Cookies。如果需要，模拟登录流程：
//javadriver.get("http://www.czce.com.cn");
//// 添加登录代码（如填写表单、点击登录按钮）

            // 禁用自动化控制特征（避免网站检测）
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--start-maximized");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--lang=zh-CN");
            WebDriver driver = new ChromeDriver(options);




            String url_b = "http://www.czce.com.cn/cn/DFSStaticFiles/Future/2025/20250801/FutureDataHolding.htm";
//            List<TransactionDetails> insertList = new ArrayList<>();
            List<String> insertList = new ArrayList<>();
            try {
                driver.get("http://www.czce.com.cn");
                driver.get(url_b);
                // 等待页面加载
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                wait.until(driver2 -> ((JavascriptExecutor) driver2)
                        .executeScript("return document.readyState").equals("complete"));

                // 保存页面源码
                String pageSource = driver.getPageSource();
                Files.writeString(Paths.get("page_source.html"), pageSource, StandardCharsets.UTF_8);

                // 检查 iframe
                List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
                log.info("iframe 数量: " + iframes.size());
                if (!iframes.isEmpty()) {
                    for (int i = 0; i < iframes.size(); i++) {
                        driver.switchTo().defaultContent();
                        driver.switchTo().frame(i);
                        log.info("检查 iframe " + i + " 内容");
                        String iframeSource = driver.getPageSource();
                        Files.writeString(Paths.get("iframe_source_" + i + ".html"), iframeSource, StandardCharsets.UTF_8);
                        try {
                            WebElement display = wait.until(ExpectedConditions.visibilityOfElementLocated(
                                    By.cssSelector("table tr:nth-child(3) td:nth-child(2)")));
                            log.info("在 iframe " + i + " 中找到目标元素: " + display.getText());
                            break;
                        } catch (TimeoutException e) {
                            log.warn("iframe " + i + " 中未找到元素");
                        }
                    }
                } else {
                    log.warn("未找到 iframe，使用主页面");
                }

                // 检查表格
                List<WebElement> tables = driver.findElements(By.tagName("table"));
                log.info("找到表格数量: " + tables.size());
                tables.forEach(table -> log.info("表格内容: " + table.getText()));

                // 尝试查找元素
                WebElement display = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("table tr:nth-child(3) td:nth-child(2)")));
                String elementText = display.getText();
                System.out.println("元素内容: " + JSONUtil.toJsonStr(elementText));

                // 获取多个元素
                List<WebElement> webElements = driver.findElements(
                        By.cssSelector("table tr:nth-child(3) td:nth-child(2)"));
                webElements.forEach(element -> insertList.add(element.getText()));
                System.out.println("元素列表: " + JSONUtil.toJsonStr(insertList));


            } catch (NoSuchElementException e) {
                System.err.println("无法定位元素: " + e.getMessage());
                // 保存页面源码和截图
                System.out.println("页面源码: " + driver.getPageSource());
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//                FileUtils.copyFile(screenshot, new File("screenshot.png"));
            } catch (Exception e) {
                System.err.println("无法定位元素: " + e.getMessage());
                // 保存页面源码和截图
                System.out.println("页面源码: " + driver.getPageSource());
                System.err.println("处理交易数据失败: " + e.getMessage() + e);
                e.printStackTrace();
            } finally {
                driver.quit();
            }
        } catch (Exception e) {
            log.error("处理交易数据失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 1-抓取数据
     *
     * @param dateList 日期集合
     * @param mail     邮箱
     */
    @Async
    public void executeAsyncTaskV1(List<String> dateList, String mail) throws IOException {
        long start = System.currentTimeMillis();
        log.info("异步任务入参：{},mail {}", dateList, mail);
        if (ObjectUtil.isEmpty(dateList)) {
            dateList.add("20250801");
            log.info("异步任务入参为空，使用默认参数: 20250801");
        }
        if (mail == null) {
            mail = "andyfaupassion@gmail.com";
            log.info("异步任务入参为空，使用默认参数 mail: andyfaupassion@gmail.com");
        }
        graspingDataV2(dateList);
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


    private static List<List<TradingData>> splitTradingDataList(List<TradingData> list) {
        List<List<TradingData>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            partitions.add(list.subList(i, Math.min(i + BATCH_SIZE, list.size())));
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
     * 2.5-获取链接
     *
     * @param driver driver
     * @return list
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
        log.info("获取链接信息结束: " + list.size());
        return list;
    }

    /**
     * 2--抓取数据
     *
     * @param dateList dateList
     */
    public void graspingDataV2(List<String> dateList) throws IOException {
        // 创建固定大小的线程池
        // 获取所有的链接
        long startTime = System.currentTimeMillis();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 如果需要无头模式
        options.addArguments("--no-sandbox"); // 添加此选项
        options.addArguments("--disable-dev-shm-usage"); // 添加此选项
        WebDriver driver = new ChromeDriver(options);
        List<String> list = fetchLinks(driver);
        log.info("\n\n一共有{}个链接", list.size());
        List<TradingData> insertList = new ArrayList<>();
//        000
//        for (String s : list) {
//            for (String month : dateList) {
//                String url = s + "/" + month;
//                try {
//                    log.info("\n\n开始抓取：{}", url);
//                    scrapeDataFromPage2(driver, url, insertList);
//                } catch (IOException e) {
//                    log.info("\n\n执行任务异常：{}", e.getMessage());
//                }
//            }
//        }
//        11
        // Create a fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        try {
            // Create list of CompletableFuture tasks
            List<CompletableFuture<Void>> futures = list.stream()
                    .flatMap(s -> dateList.stream()
                            .map(month -> {
                                String url = s + "/" + month;
                                return CompletableFuture.runAsync(() -> {
                                    try {
                                        log.info("\n" + atomicInteger.getAndIncrement() + "---开始抓取：{},------thread:{}", url, Thread.currentThread().getName());
                                        /*
                                         * 抓取数据
                                         */
                                        scrapeDataFromPage2(driver, url, insertList);
                                    } catch (IOException e) {
                                        log.error("执行任务异常 for URL {}: {}", url, e.getMessage());
                                    }
                                }, executor);
                            }))
                    .toList();

            // Wait for all tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } finally {
            // Shutdown the executor
            executor.shutdown();
        }
        log.info("抓取数据耗时：" + (System.currentTimeMillis() - startTime) + "ms = " + (System.currentTimeMillis() - startTime) / 1000 + "s");
//        22
        log.info("插入的数据量：{}", insertList.size());
        driver.quit();
        // 创建线程池
        long l = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

//        000
//        List<List<TradingData>> tradingDataList = splitTradingDataList(insertList);
//        for (List<TradingData> tradingList : tradingDataList) {
//            executorService.submit(() -> {
//                log.info("\n\n----开始插入数据：{}", JSONUtil.toJsonStr(tradingList));
//                // 模拟数据库提交操作
//                tradingDataMapper.insertList(tradingList);
//            });
//        }
//        log.info("插入数据耗时：" + (System.currentTimeMillis() - l) + "ms = " + (System.currentTimeMillis() - l) / 1000 + "s");
//        // 关闭线程池
//        executorService.shutdown();
//        111
        try {
            AtomicInteger atomicInsert = new AtomicInteger(0);

            /**
             * 组装数据
             */
            List<List<TradingData>> tradingDataList = splitTradingDataList(insertList);
            /**
             * Create and submit async tasks
             */
            List<List<TradingData>> batches = splitIntoBatches(insertList, BATCH_SIZE);
            log.info("Total batches to process: {}", batches.size());

            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch -> CompletableFuture.runAsync(() -> {
                        int retries = 0;
                        while (retries < MAX_RETRIES) {
                            try {
                                log.info("开始插入数据 (batch size: {})", batch.size());
                                tradingDataMapper.insertList(batch);
                                return;
                            } catch (Exception e) {
                                retries++;
                                log.warn("插入数据失败 for batch (attempt {}/{}): {}", retries, MAX_RETRIES, e.getMessage());
                                if (retries == MAX_RETRIES) {
                                    log.error("插入数据失败 after max retries for batch: {}", e.getMessage());
                                    throw new RuntimeException("Batch insert failed after " + MAX_RETRIES + " attempts", e);
                                }
                                try {
                                    Thread.sleep(1000L * retries); // Exponential backoff
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeException("Retry interrupted", ie);
                                }
                            }
                        }
                    }, executorService))
                    .toList();
//            List<CompletableFuture<Void>> futures = tradingDataList.stream()
//                    .map(tradingList -> CompletableFuture.runAsync(() -> {
//                        try {
//                            log.info("开始插入数据：{}", JSONUtil.toJsonStr(tradingList));
//                            tradingDataMapper.insertList(tradingList);
//                        } catch (Exception e) {
//                            log.error("插入数据失败 for batch: {}", e.getMessage());
//                            throw new RuntimeException("Batch insert failed", e);
//                        }
//                    }, executorService))
//                    .toList();

            // Wait for all tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            long duration = System.currentTimeMillis() - startTime;
            log.info("插入数据耗时：{}ms = {}s", duration, duration / 1000.0);
        } catch (Exception e) {
            log.error("批量插入数据异常：{}", e.getMessage());
            throw e;
        } finally {
            // Graceful shutdown
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(AWAIT_TERMINATION_SECONDS, TimeUnit.SECONDS)) {
                    log.warn("线程池未在指定时间内关闭，强制关闭");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("线程池关闭被中断：{}", e.getMessage());
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
//        22
    }

    private List<List<TradingData>> splitIntoBatches(List<TradingData> list, int batchSize) {
        List<List<TradingData>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    private static final int WAIT_TIMEOUT_MS = 500;
    private static final By DISPLAY_SELECTOR = By.cssSelector("body > div.desktop > div > div:nth-child(1) > div");
    private static final By SWITCH_BUTTON_SELECTOR = By.cssSelector("#switch");
    private static final By TABLE_CONTAINER_SELECTOR = By.cssSelector("body > div.type_table > div > div");

    public void scrapeDataFromPage2(WebDriver driver, String url, List<TradingData> insertList) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        // 使用线程安全的集合
        List<TradingData> localInsertList = new CopyOnWriteArrayList<>();
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    driver.get(url);
                    log.info("已导航至URL: {}", url);

                    // 缓存显示元素
                    WebElement display = driver.findElement(DISPLAY_SELECTOR);

                    // 检查是否需要切换到表格视图
                    if (!display.getAttribute("style").contains("display: none")) {
                        log.info("切换到表格视图");
                        driver.findElement(SWITCH_BUTTON_SELECTOR).click();
                        new WebDriverWait(driver, Duration.ofMillis(WAIT_TIMEOUT_MS))
                                .until(ExpectedConditions.visibilityOfElementLocated(TABLE_CONTAINER_SELECTOR));
                    }

                    log.info("开始获取表格数据");
                    Result result = getResult(driver);


                    // 并行处理多单数据
                    CompletableFuture<Void> buyFuture = CompletableFuture.runAsync(() ->
                            result.table_buy().parallelStream()
                                    .filter(element -> !element.getAttribute("class").contains("opacity05"))
                                    .forEach(element -> getRowData(element, result.tableBuyTileArray(),
                                            result.table_buy_title_list(), result.data())), executor);

//                      // 多单
//        for (WebElement webElement : result.table_buy()) {
//            if (webElement.getAttribute("class").contains("opacity05")) {
//                continue;
//            }
//            getRowData(webElement, result.tableBuyTileArray(), result.table_buy_title_list(), result.data());
//        }
                    // 异步处理空单数据
//                    getTableSale(result.table_sale(), result.table_sale_title_list(), result.tableSaleTileArray(), result.data());
//
                    CompletableFuture<Void> saleFuture = CompletableFuture.runAsync(() ->
                            getTableSale(result.table_sale(), result.table_sale_title_list(),
                                    result.tableSaleTileArray(), result.data()), executor);

                    // 等待数据处理完成
                    CompletableFuture.allOf(buyFuture, saleFuture).join();


//                     // 组装数据
//        log.info("开始组装数据");
//        for (List<String> datum : result.data()) {
//            if (datum.size() == 7) {
//                TradingData tradingData = new TradingData();
//                tradingData.setSeat(datum.get(0));
//                try {
//                    addTradingData(insertList, datum, tradingData);
//                } catch (NumberFormatException e) {
//                    System.out.println("详情异常" + e.getMessage());
//                }
//            }
//        }
                    // 并行组装最终数据
                    result.data().parallelStream()
                            .filter(datum -> datum.size() == 7)
                            .forEach(datum -> {
                                TradingData tradingData = new TradingData();
                                tradingData.setSeat(datum.get(0));
                                try {
                                    addTradingData(localInsertList, datum, tradingData);
                                } catch (NumberFormatException e) {
                                    log.error("数据处理异常: {}", e.getMessage());
                                }
                            });

                    log.info("完成URL {} 的数据抓取", url);
                } catch (Exception e) {
                    log.error("抓取URL {} 失败: {}", url, e.getMessage());
                    throw new RuntimeException("抓取失败", e);
                }
            }, executor);
            future.join();
            insertList.addAll(localInsertList);
        } catch (Exception e) {
            log.error("异步抓取异常: {}", e.getMessage());
            throw new IOException("异步抓取失败", e);
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.warn("线程池未在指定时间内关闭，强制关闭");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("线程池关闭被中断: {}", e.getMessage());
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 2-
     *
     * @param driver
     * @param url
     * @param insertList
     * @throws IOException
     */
    private void scrapeDataFromPage3(WebDriver driver, String url, List<TradingData> insertList) throws IOException {
        driver.get(url);
        // 找到对应的数据地址
        log.info("找到对应的数据地址");
        WebElement display = driver.findElement(By.cssSelector("body > div.desktop > div > div:nth-child(1) > div"));
        // 检测是否以表格形式打开
        log.info("检测是否以表格形式打开");
        String style = display.getAttribute("style");
        if (!style.contains("display: none")) {
            WebElement button = driver.findElement(By.cssSelector("#switch"));
            // 模拟点击事件（切换表格显示）
            log.info("模拟点击事件（切换表格显示）");
            button.click();
            // 等待10秒页面刷新
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.type_table > div > div")));
        }
        log.info("查找特定元素下的表格");

        // 查找特定元素下的表格
        Result result = getResult(driver);
        // 使用 LambdaQueryWrapper 构造删除条件
//        LambdaQueryWrapper<TradingData> queryWrapper = new LambdaQueryWrapper<>();
        // 将传入的日期的数据全部删除，重新插入
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
        log.info("开始组装数据");
        for (List<String> datum : result.data()) {
            if (datum.size() == 7) {
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
}
