package com.project.demo.code.selenium.guangZhou;

/**
 * @Description
 * @Author Andy Fan
 * @Date 2025/8/8 13:47
 * @ClassName sda
 */

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DceSeleniumDownload {
    public static void main(String[] args) throws InterruptedException {
//        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        String downloadFilepath = Paths.get("./andyFile").toAbsolutePath().toString();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadFilepath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get("http://www.dce.com.cn/publicweb/quotesdata/memberDealPosiQuotes.html");

            // 根据页面元素选择日期、合约等（可能需要 JS 操作）
            // driver.findElement(By.id("xxx")).click();

            // 点击导出按钮
            driver.findElement(By.id("batchExportId")).click();

            Thread.sleep(5000); // 等待下载完成
            System.out.println("文件已下载到 " + downloadFilepath);
        } finally {
            driver.quit();
        }
    }
}
