package com.project.demo.task.selenium.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @Description 获取以下四个交易所的期货当日交易数据
 * //上海期货交易所：https://www.shfe.com.cn/reports/tradedata/dailyandweeklydata/
 * //郑州商品交易所：czce.com.cn/cn/jysj/ccpm/H770304index_1.htm
 * //大连商品交易所：http://www.dce.com.cn/dalianshangpin/xqsj/tjsj26/jdtj/rcjccpm/index.html
 * //广州期货交易所：http://www.gfex.com.cn/gfex/rcjccpm/hqsj_tjsj.shtml
 * @Author Andy Fan
 * @Date 2025/7/27 18:43
 * @ClassName seleniumService
 */
@Service
@Slf4j
public class SeleniumService {
    public static void main(String[] args) {
        //打开百度浏览器
        System.out.println("hello world");
        WebDriver driver = new ChromeDriver();
        //上海期货交易所：https://www.shfe.com.cn/reports/tradedata/dailyandweeklydata/
        //郑州商品交易所：czce.com.cn/cn/jysj/ccpm/H770304index_1.htm
        //大连商品交易所：http://www.dce.com.cn/dalianshangpin/xqsj/tjsj26/jdtj/rcjccpm/index.html
        //广州期货交易所：http://www.gfex.com.cn/gfex/rcjccpm/hqsj_tjsj.shtml

        driver.get("https://www.shfe.com.cn/reports/tradedata/dailyandweeklydata/");
        //获取<title></title>里面的信息
        String title = driver.getTitle();
        System.out.println("title : " + title);

        try {
            WebDriver.Timeouts timeouts = driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
            System.out.println("timeouts : " + timeouts);
            //<span id = "pm"></span>
            WebElement pm = driver.findElement(By.id("pm"));
            System.out.println("pm : " + pm);

            WebElement submitButton = driver.findElement(By.cssSelector("button"));
//            System.out.println("submitButton : " + submitButton);
//            pm.sendKeys("Selenium");
            pm.click();

            WebElement message = driver.findElement(By.id("message"));
            System.out.println("message : " + message);
            message.getText();

            System.out.println("message : " + message.getText());
        } catch (Exception e) {
            log.info("\n获取数据错误:\n" + e.getMessage());
            log.info("\n获取数据错误:\n" + e);
        } finally {
            log.info("\n关闭浏览器");
            driver.quit();
        }
    }


}
