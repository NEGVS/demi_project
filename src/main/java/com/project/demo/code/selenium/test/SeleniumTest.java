package com.project.demo.code.selenium.test;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

/**
 * @Description
 * @Author Andy Fan
 * @Date 2025/7/22 10:15
 * @ClassName seleniumTest
 */
public class SeleniumTest {
    public static void main(String[] args) {
        //打开百度浏览器
        System.out.println("hello world");
        WebDriver driver = new ChromeDriver();
        //上海期货交易所：https://www.shfe.com.cn/reports/tradedata/dailyandweeklydata/
        //郑州商品交易所：czce.com.cn/cn/jysj/ccpm/H770304index_1.htm
        //大连商品交易所：http://www.dce.com.cn/dalianshangpin/xqsj/tjsj26/jdtj/rcjccpm/index.html
        //广州期货交易所：http://www.gfex.com.cn/gfex/rcjccpm/hqsj_tjsj.shtml

        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        //获取<title></title>里面的信息
        String title = driver.getTitle();
        System.out.println("title : " + title);

        WebDriver.Timeouts timeouts = driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        System.out.println("timeouts : " + timeouts);
        //<input type="text" class="form-control" name="my-text" id="my-text-id" myprop="myvalue">
        WebElement textBox = driver.findElement(By.name("my-text"));
        System.out.println("texBox : " + textBox);

        //<button type="submit" class="btn btn-outline-primary mt-3">Submit</button>
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        System.out.println("submitButton : " + submitButton);
        textBox.sendKeys("Selenium");
        submitButton.click();

        WebElement message = driver.findElement(By.id("message"));
        System.out.println("message : " + message);
        message.getText();

        System.out.println("message : " + message.getText());
        driver.quit();
    }
}
