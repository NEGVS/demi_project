package com.project.demo.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.project.demo.code.domain.TradingData;
import com.project.demo.code.mapper.TradingDataMapper;
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
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AsyncTaskService {
    private static final int AWAIT_TERMINATION_SECONDS = 30;


    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2; // M2 10核，设置12个线程以平衡性能核和效率核
    private static final int AWAIT_TERMINATION_MINUTES = 60; // 最大等待时间1小时
    private static final int BATCH_SIZE = 1000; // 数据库批量插入大小
    private static final int MAX_RETRIES = 3; // 最大重试次数
    private static final int PAGE_LOAD_TIMEOUT_SECONDS = 5; // 页面加载超时
    private static final int ELEMENT_WAIT_MILLIS = 500; // 元素等待时间

    @Resource
    private TradingDataMapper tradingDataMapper;

    @Resource
    private JavaMailSender mailSender;

    /**
     * 1-抓取数据
     *
     * @param dateList dateList
     * @param mail     mail
     */
    @Async
    public void executeAsyncTaskV2(List<String> dateList, String mail) {
        long start = System.currentTimeMillis();
        log.info("异步任务入参：{}， mail:{}", dateList, mail);
        graspingDataV4(dateList);
        log.info("异步任务执行完毕，耗时：" + (System.currentTimeMillis() - start) + "毫秒");
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
     * 分割 TradingData 列表为批次
     */
    private static List<List<TradingData>> splitTradingDataList(List<TradingData> list) {
        List<List<TradingData>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += AsyncTaskService.BATCH_SIZE) {
            partitions.add(list.subList(i, Math.min(i + AsyncTaskService.BATCH_SIZE, list.size())));
        }
        return partitions;
    }

    /**
     * 从表格行提取数据
     */
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

    /**
     * 4-组装数据
     */
    public static void addTradingData(ConcurrentLinkedQueue<TradingData> insertList, List<String> datum, TradingData tradingData) {
        tradingData.setLongPosition(Integer.valueOf(datum.get(1)));
        tradingData.setChangeNum(Integer.valueOf(datum.get(2)));
        tradingData.setNetLongShort(Integer.valueOf(datum.get(3)));
        tradingData.setDate(DateUtil.parse(datum.get(4)));
        tradingData.setCommodity(datum.get(5));
        tradingData.setType(datum.get(6));
        insertList.add(tradingData);
    }

    /**
     * 获取表格数据
     */
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

    /**
     * 处理空单表格
     */
    static void getTableSale(List<WebElement> table_sale, List<String> table_sale_title_list, String[] tableSaleTileArray, List<List<String>> data) {
        for (WebElement webElement : table_sale) {
            getRowData(webElement, tableSaleTileArray, table_sale_title_list, data);
        }
    }

    /**
     * 获取所有链接
     */
    private List<String> fetchLinks(WebDriver driver) {
        driver.get("https://cc.17kqh.com/");
//        000
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//        111
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(PAGE_LOAD_TIMEOUT_SECONDS));
        log.info("获取链接信息开始");
//000
//        List<WebElement> webElements = driver.findElements(By.cssSelector("body > div.container > div:nth-child(3) > div > div > div > ul > li > a"));
//        111
        List<WebElement> webElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("body > div.container > div:nth-child(3) > div > div > div > ul > li > a")));
        List<String> list = new ArrayList<>();
        for (WebElement webElement : webElements) {
            list.add(webElement.getAttribute("href"));
        }
        log.info("获取链接完成，共有 {} 个链接", list.size());
        return list;
    }

    /**
     * 2-并行抓取数据 循环链接
     *
     * @param dateList dateList
     */
    public void graspingDataV4(List<String> dateList) {
        // 创建固定大小的线程池
        // 获取所有的链接
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // 如果需要无头模式，使用新版无头模式，减少资源占用
        options.addArguments("--no-sandbox"); // 添加此选项
        options.addArguments("--disable-dev-shm-usage"); // 添加此选项
//        options.addArguments("--disable-gpu"); // 禁用 GPU 加速
        options.addArguments("--blink-settings=imagesEnabled=false"); // 禁用图片加载
        options.setPageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT_SECONDS));
        WebDriver tempDriver = new ChromeDriver(options);

        try {
            // 获取链接
            List<String> links = fetchLinks(tempDriver);
//        List<String> list = list22.subList(0, 10);
            log.info("\n\n一共有{}个链接", links.size());
//        List<TradingData> insertList = new ArrayList<>();
            ConcurrentLinkedQueue<TradingData> insertList = new ConcurrentLinkedQueue<>();
            long l0 = System.currentTimeMillis();
            AtomicInteger atomicInteger = new AtomicInteger(0);

//        000
            int totalTasks = links.size() * dateList.size();
            log.info("共需处理 {} 个任务", totalTasks);
            // 预生成所有 URL
            List<String> urls = new ArrayList<>(totalTasks);
            for (String link : links) {
                for (String month : dateList) {
                    urls.add(link + "/" + month);
                }
            }

//        for (String s : links) {
//            for (String month : dateList) {
//                String url = s + "/" + month;
//                try {
//                    log.info("\n" + atomicInteger00.getAndIncrement() + " of All: " + all + " -----开始抓取：{},------thread:{}", url, Thread.currentThread().getName());
//                    scrapeDataFromPage2(driver, url, insertList);
//                } catch (IOException e) {
//                    log.info("\n\n执行任务异常：{}", e.getMessage());
//                }
//            }
//        }

            // 创建线程池
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            try {
                // 提交任务
                for (String url : urls) {
                    executor.submit(() -> {
                        WebDriver driver = null;
                        try {
                            driver = new ChromeDriver(options);
                            int attempt = 0;
                            while (attempt < MAX_RETRIES) {
                                try {
                                    log.info("{} of {} -----开始抓取：{}, thread: {}",
                                            atomicInteger.getAndIncrement(), totalTasks, url, Thread.currentThread().getName());
                                    scrapeDataFromPage2(driver, url, insertList);
                                    break; // 成功后退出重试
                                } catch (IOException | TimeoutException e) {
                                    attempt++;
                                    if (attempt == MAX_RETRIES) {
                                        log.error("抓取失败 URL: {} 错误: {}", url, e.getMessage());
                                    } else {
                                        log.warn("抓取失败 URL: {}, 重试 {}/{}", url, attempt, MAX_RETRIES);
                                        Thread.sleep(new Random().nextInt(500) + 500); // 随机延时 0.5-1s
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("任务执行异常 URL: {}: {}", url, e.getMessage());
                        } finally {
                            if (driver != null) {
                                driver.quit(); // 确保关闭 WebDriver
                            }
                        }
                    });
                }
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(AWAIT_TERMINATION_MINUTES, TimeUnit.MINUTES)) {
                        log.warn("任务未在 {} 分钟内完成，强制终止", AWAIT_TERMINATION_MINUTES);
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    log.error("线程池关闭中断: {}", e.getMessage());
                    executor.shutdownNow();
                }
            }
            long l1 = System.currentTimeMillis();
////        1111
//        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//        AtomicInteger atomicInteger = new AtomicInteger(0);
//        try {
//            // Create list of CompletableFuture tasks
//            List<CompletableFuture<Void>> futures = list.stream()
//                    .flatMap(s -> dateList.stream()
//                            .map(month -> {
//                                String url = s + "/" + month;
//                                return CompletableFuture.runAsync(() -> {
//                                    try {
//                                        log.info("\n" + atomicInteger.getAndIncrement() + "---开始抓取：{},------thread:{}", url, Thread.currentThread().getName());
//                                        /*
//                                         * 抓取数据
//                                         */
//                                        scrapeDataFromPage2(driver, url, insertList222);
//                                    } catch (IOException e) {
//                                        log.error("执行任务异常 for URL {}: {}", url, e.getMessage());
//                                    }
//                                }, executor);
//                            }))
//                    .toList();
//            // Wait for all tasks to complete
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//        } finally {
//            // Shutdown the executor
//            executor.shutdown();
//        }
            long l2 = System.currentTimeMillis();
            log.info("111抓取数据耗时：" + (l1 - l0) + "ms = " + (l1 - l0) / 1000 + "s");
            log.info("222抓取数据耗时：" + (l2 - l1) + "ms = " + (l2 - l1) / 1000 + "s");
//        22
            log.info("111插入的数据量：{}", insertList.size());
//        log.info("222插入的数据量：{}", insertList222.size());
//        driver.quit();
            // 创建线程池
//        ExecutorService executorService = Executors.newFixedThreadPool(10 * 2);
//
//        List<List<TradingData>> tradingDataList = splitTradingDataList(insertList);
//        for (List<TradingData> tradingList : tradingDataList) {
//            executorService.submit(() -> {
//                log.info("\n\n----开始插入数据：{}", JSONUtil.toJsonStr(tradingList));
//                // 模拟数据库提交操作
//                tradingDataMapper.insertList(tradingList);
//            });
//        }
//        // 关闭线程池--
//        executorService.shutdown();

            // 批量插入数据库
            List<TradingData> finalInsertList = new ArrayList<>(insertList);
            if (!finalInsertList.isEmpty()) {
                ExecutorService dbExecutor = Executors.newFixedThreadPool(10); // 数据库插入线程池
                try {
                    List<List<TradingData>> batches = splitTradingDataList(finalInsertList);
                    for (List<TradingData> batch : batches) {
                        dbExecutor.submit(() -> {
                            try {
                                tradingDataMapper.insertList(batch);
                                log.info("插入数据批次，数量: {}", batch.size());
                            } catch (Exception e) {
                                log.error("数据库插入失败: {}", e.getMessage());
                            }
                        });
                    }
                } finally {
                    dbExecutor.shutdown();
                    try {
                        if (!dbExecutor.awaitTermination(10, TimeUnit.MINUTES)) {
                            log.warn("数据库插入未在 10 分钟内完成，强制终止");
                            dbExecutor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        log.error("数据库线程池关闭中断: {}", e.getMessage());
                        dbExecutor.shutdownNow();
                    }
                }
            }

        } finally {
            tempDriver.quit();

        }
    }

    /**
     * 3- 抓取单页数据 实现方法
     *
     * @param url        url
     * @param insertList insertList
     * @throws IOException IOException
     */
    private void scrapeDataFromPage2(WebDriver driver, String url, ConcurrentLinkedQueue<TradingData> insertList) throws IOException, TimeoutException {
        long l = System.currentTimeMillis();
        driver.get(url);
        // 找到对应的数据地址
        WebElement display = driver.findElement(By.cssSelector("body > div.desktop > div > div:nth-child(1) > div"));
        // 检测是否以表格形式打开
        String style = display.getAttribute("style");
        if (!style.contains("display: none")) {
            WebElement button = driver.findElement(By.cssSelector("#switch"));
            // 模拟点击事件（切换表格显示）
            button.click();
            // 等待1秒页面刷新--随机延迟
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(new Random().nextInt(500)));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.type_table > div > div")));
        }
        //// 获取表格数据 查找特定元素下的表格
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
        long l2 = System.currentTimeMillis();
//              driver.get(url);用时：171 //2025-07-30 10:44:41
//              >>> 截止到组装数据用时：735
        // 组装数据
        AtomicInteger atomicInteger1 = new AtomicInteger(0);
        for (List<String> datum : result.data()) {
            log.info(atomicInteger1.getAndIncrement() + "--------------------开始组装数据：{},------thread:{}", url, Thread.currentThread().getName());
            if (datum.size() == 7) {
                TradingData tradingData = new TradingData();
                tradingData.setSeat(datum.get(0));
                try {
                    addTradingData(insertList, datum, tradingData);
                } catch (NumberFormatException e) {
                    log.warn("数据组装异常 URL: {}: {}", url, e.getMessage());
                }
            }
        }
//        log.info("抓取 URL: {} 完成，耗时: {} ms,---thread:{}", url, System.currentTimeMillis() - l, Thread.currentThread().getName());
    }

    private record Result(List<WebElement> table_buy, List<WebElement> table_sale, List<String> table_buy_title_list,
                          String[] tableBuyTileArray, List<String> table_sale_title_list, String[] tableSaleTileArray,
                          List<List<String>> data) {
    }
}
