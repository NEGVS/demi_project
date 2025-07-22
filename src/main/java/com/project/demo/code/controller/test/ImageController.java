package com.project.demo.code.controller.test;

import cn.hutool.core.collection.CollUtil;
import com.project.demo.DemoApplication;
import com.project.demo.code.domain.DemoChickenSoup;
import com.project.demo.code.mapper.DemoChickenSoupMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

@RestController
@RequestMapping("/image")
public class ImageController {

    @jakarta.annotation.Resource
    private DemoChickenSoupMapper demoChickenSoupMapper;


    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 读取上传的主图片
            BufferedImage mainImage = ImageIO.read(file.getInputStream());

            // 2. 读取 logo 图片（假设logo.png在resources/static目录下）
            InputStream logoStream = getClass().getResourceAsStream("/static/logo.png");
            if (logoStream == null) {
                return "Error: logo.png not found in resources/static/";
            }
            BufferedImage logoImage = ImageIO.read(logoStream);

            // 3. 创建 Graphics2D 对象
            Graphics2D g2d = mainImage.createGraphics();

            // 4. 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 5. 添加 logo（左上角）
            int logoX = 10;
            int logoY = 10;
            int logoWidth = logoImage.getWidth();
            int logoHeight = logoImage.getHeight();
            g2d.drawImage(logoImage, logoX, logoY, logoWidth * 2, logoHeight * 2, null);

            // 6. 添加时间文字（右上角）
            String timeText = getCurrentTime();
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Noto Sans CJK SC", Font.BOLD, 150));
            FontMetrics fmTime = g2d.getFontMetrics();
            int timeTextWidth = fmTime.stringWidth(timeText);
            int timeTextHeight = fmTime.getHeight();
            int timeTextX = mainImage.getWidth() - timeTextWidth - 10;
            int timeTextY = 10 + timeTextHeight;
            g2d.setColor(new Color(0, 0, 0, 0)); // 半透明黑色背景
            g2d.fillRoundRect(timeTextX - 5, timeTextY - timeTextHeight + 5, timeTextWidth + 10, timeTextHeight, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString(timeText, timeTextX, timeTextY);
            List<DemoChickenSoup> demoChickenSoups = demoChickenSoupMapper.selectAll(null);
            String sloganText = "美丽不是等待岁月的恩赐，而是主动出击的勇气。每一次蜕变，都是为了更自信的笑容；每一次尝试，都是在为美丽加分。相信自己，你终将绽放光芒。";
            if (CollUtil.isNotEmpty(demoChickenSoups)) {
                Random random = new Random();
                long l = random.nextLong(demoChickenSoups.size());
                // 随机获取一条鸡汤
                DemoChickenSoup demoChickenSoup = demoChickenSoups.get((int) l);
                sloganText = demoChickenSoup.getContent();
            }
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Noto Sans CJK SC", Font.PLAIN, 150));
            int margin = 20;
            int maxWidth = mainImage.getWidth() - 2 * margin;
            FontMetrics fmSlogan = g2d.getFontMetrics();
            List<String> lines = wrapText(sloganText, fmSlogan, maxWidth);
            int lineHeight = fmSlogan.getHeight();
            int totalTextHeight = lines.size() * lineHeight;
            int sloganTextY = mainImage.getHeight() - 50 - totalTextHeight;
            int maxLineWidth = 0;
            for (String line : lines) {
                maxLineWidth = Math.max(maxLineWidth, fmSlogan.stringWidth(line));
            }
            int backgroundX = (mainImage.getWidth() - maxLineWidth) / 2 - 10;
            int backgroundY = sloganTextY - lineHeight + 5;
            int backgroundWidth = maxLineWidth + 20;
            int backgroundHeight = totalTextHeight + 10;
            g2d.setColor(new Color(0, 0, 0, 0)); // 半透明黑色背景
            g2d.fillRoundRect(backgroundX, backgroundY, backgroundWidth, backgroundHeight, 20, 20);
            g2d.setColor(Color.BLACK);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int lineWidth = fmSlogan.stringWidth(line);
                int lineX = (mainImage.getWidth() - lineWidth) / 2;
                g2d.drawString(line, lineX, sloganTextY + i * lineHeight);
            }

            // 8. 释放 Graphics2D 资源
            g2d.dispose();

            // 9. 保存处理后的图片
//            String outputFileName = fastImageMD5(mainImage) + ".jpg";
            String outputFileName = UUID.randomUUID().toString() + ".jpg";
            File outputFile = new File("uploads/" + outputFileName);
            outputFile.getParentFile().mkdirs(); // 确保uploads目录存在
            ImageIO.write(mainImage, "jpg", outputFile);
            // 10. 返回下载链接
            return "http://1.92.111.247:" + DemoApplication.tempPort + "/image/download/" + outputFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing image: " + e.getMessage();
        }
    }

    public static String fastImageMD5(BufferedImage image) {
        try {
            // 使用行扫描直接获取像素数据
            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            // 将像素数据转换为字节
            ByteBuffer buffer = ByteBuffer.allocate(pixels.length * 4);
            for (int pixel : pixels) {
                buffer.putInt(pixel);
            }

            // 计算哈希（可替换为MD5）
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer.array());
            byte[] digest = md.digest();

            // 转换为十六进制
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("快速MD5计算失败", e);
        }
    }


    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            // 构建文件路径
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // 检查文件是否存在
            if (resource.exists() && resource.isReadable()) {
                // 设置响应头，强制下载
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    // 获取当前时间的方法
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
        return sdf.format(new Date());
    }

    // 自动换行方法
    private static List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentWidth = 0;

        for (char c : text.toCharArray()) {
            int charWidth = fm.charWidth(c);
            if (currentWidth + charWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentWidth = 0;
            }
            currentLine.append(c);
            currentWidth += charWidth;
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}
