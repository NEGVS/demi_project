package com.project.demo.code.selenium.guangZhou;

/**
 * @Description
 * @Author Andy Fan
 * @Date 2025/8/8 13:42
 * @ClassName ddd
 */

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class DceDownloader {

    public static void main(String[] args) throws Exception {
        String url = "http://www.dce.com.cn/publicweb/quotesdata/exportMemberDealPosiQuotesBatchData.html";

        // POST 参数， "&year=2025&month=7&day=04" + 表示 实际 2025-08-04
        String formData = "memberDealPosiQuotes.variety=b" +
                "&memberDealPosiQuotes.trade_type=0" +
                "&contract.contract_id=b2509" +
                "&contract.variety_id=b" +
                "&year=2025&month=07&day=04" +
                "&batchExportFlag=batch";

        HttpPost post = new HttpPost(url);

        // 设置请求头
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8");
        post.setHeader("Cache-Control", "max-age=0");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Cookie", "JSESSIONID=D9911CA87C37D9C31D65B20FDEDC27F0; hNUS9DnJtejwS=60H1A2qnmPqH_hl7kmXuAT6pAEKU_7YDqUcRNdA2c4WXSqy0jIxi6US5IpMz_dP.YlG1WiQp5evEwEyi09ckXJdq; Hm_lvt_a50228174de2a93aee654389576b60fb=1754284594; HMACCOUNT=A13C01A2DEF301AA; Hm_lpvt_a50228174de2a93aee654389576b60fb=1754631241; hNUS9DnJtejwT=0MYQ2qHwDR_6l8ntZcxn0wTgMDfE4AHpu7nhZxu7m9438RiVNtbspM8BRwX5j5epFQzXxVnn_DWdUJm1hU9_o34ugK_jJNATdD.l2Q7adM_KPTmCrnX1lRWYXvJBGsq9c7PBJm14Qsnxf1Nw1Xco9UePhA3nU3BgvgcPhScapbrhvSWOB8vEuoUmwTNkK5AfmwyVM2FV1TAbfL01nvcIqgVBlMM8BH93kspoLB1jzAAZdj6gp4UkQY3ia.UhX1Eh.0Q3B.PkuxjZBWvDA.C3fd5FACd6Yo3j29VwNfhMDo3X2mFGKGMHDupNXvl5X4J7x1cmGBUUNr4WZVSqYBY4YAzRvd0IwaOMP3a_CWoeCoZo5cmyuKIB_yolgD9VMMxYlUpSiFV2LKx5dmqzwCA5EDkbd_9FQZm2hwUo9zaoUm9i4ZV1tTq6.rA5m5GJF55RHUFAatecCKBFvJoL8YlkH8AYdtOaVuayJ0K1ljOi5t_9");
        post.setHeader("Host", "www.dce.com.cn");
        post.setHeader("Origin", "http://www.dce.com.cn");
        post.setHeader("Referer", "http://www.dce.com.cn/publicweb/quotesdata/memberDealPosiQuotes.html");
        post.setHeader("Upgrade-Insecure-Requests", "1");
        post.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36");

        // 设置 POST 表单数据
        post.setEntity(new StringEntity(formData, StandardCharsets.UTF_8));

        String saveDir = "/Users/andy_mac/Documents/CodeSpace/andyProject0/demi_project/src/main/java/com/project/demo/code/selenium/guangZhou/file";
        Files.createDirectories(Paths.get(saveDir));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 保存 zip
                String zipPath = saveDir + "/dce_data" + System.currentTimeMillis() + ".zip";
                try (InputStream is = entity.getContent();
                     FileOutputStream fos = new FileOutputStream(zipPath)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("ZIP 文件已下载：" + zipPath);

                // 解压 zip
                unzip(zipPath, saveDir);
            }
        }
    }
    public static void unzip(String zipFilePath, String destDir) throws IOException {
        System.out.println("开始解压 ZIP 文件..." + zipFilePath);
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 这里强制使用 GBK，避免中文文件名解压异常
        try (ZipFile zipFile = new ZipFile(zipFilePath, Charset.forName("GBK"))) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File outFile = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    // 确保父目录存在
                    outFile.getParentFile().mkdirs();
                    try (InputStream is = zipFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
        System.out.println("ZIP 解压完成，文件已保存到：" + destDir);
    }

    // 解压方法
    public static void unzip2(String zipFilePath, String destDir) throws IOException {
        System.out.println("开始解压 ZIP 文件..." + zipFilePath);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (Exception e) {
                        System.out.println("ZIP 解压失败");
                        System.out.println(e.getMessage());
                        System.out.println(e);
                    }
                }
                zis.closeEntry();
            }
        } catch (Exception e) {
            System.out.println("----ZIP 解压失败");
            System.out.println(e.getMessage());
            System.out.println(e);
        }
        System.out.println("ZIP 解压完成，文件已保存到：" + destDir);
    }
}
