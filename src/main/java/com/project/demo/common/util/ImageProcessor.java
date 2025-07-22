package com.project.demo.common.util;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageProcessor {

    public static ByteArrayOutputStream processImage(BufferedImage mainImage, File logoImageFile) throws Exception {
        // 读取 logo 图片
        BufferedImage logoImage = ImageIO.read(logoImageFile);

        // 创建 Graphics2D 对象，用于在主图片上绘图
        Graphics2D g2d = mainImage.createGraphics();

        // 设置抗锯齿（提高文字和图片的绘制质量）
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 在指定位置添加 logo（左上角）
        int logoX = 10; // logo 的 x 坐标
        int logoY = 10; // logo 的 y 坐标
        int logoWidth = logoImage.getWidth();  // 保持 logo 原宽度
        int logoHeight = logoImage.getHeight(); // 保持 logo 原高度
        g2d.drawImage(logoImage, logoX, logoY, logoWidth * 2, logoHeight * 2, null);

        // 在指定位置添加时间文字（右上角）
        String timeText = getCurrentTime(); // 获取当前时间作为文字
        g2d.setColor(Color.WHITE); // 设置文字颜色
        g2d.setFont(new Font("Noto Sans SC", Font.BOLD, 150)); // 设置字体和大小

        // 计算时间文字的宽度和高度，以便右对齐
        FontMetrics fmTime = g2d.getFontMetrics();
        int timeTextWidth = fmTime.stringWidth(timeText);
        int timeTextHeight = fmTime.getHeight();
        int timeTextX = mainImage.getWidth() - timeTextWidth - 10; // 右上角，距离右边 10 像素
        int timeTextY = 10 + timeTextHeight; // 右上角，距离顶部 10 像素

        // 添加时间文字背景（可选，增强可读性）
        g2d.setColor(new Color(0, 0, 0, 0)); // 半透明黑色背景
        g2d.fillRoundRect(timeTextX - 5, timeTextY - timeTextHeight + 5, timeTextWidth + 10, timeTextHeight, 10, 10);

        // 绘制时间文字
        g2d.setColor(Color.WHITE); // 重新设置文字颜色
        g2d.drawString(timeText, timeTextX, timeTextY);

        // 在主图片底部中间位置添加鸡汤文案（支持自动换行）
        String sloganText = "不要害怕追求完美，只怕你没有勇气去实现。努力改变，不是为了取悦他人，而是为了成为更好的自己。你的坚持，终将收获美丽。";
        g2d.setColor(Color.WHITE); // 设置鸡汤文案颜色
        g2d.setFont(new Font("Noto Sans SC", Font.PLAIN, 150)); // 设置字体和大小

        // 设置左右边距
        int margin = 20; // 左右边距 20 像素
        int maxWidth = mainImage.getWidth() - 2 * margin; // 文案区域的最大宽度

        // 自动换行
        FontMetrics fmSlogan = g2d.getFontMetrics();
        List<String> lines = wrapText(sloganText, fmSlogan, maxWidth);
        int lineHeight = fmSlogan.getHeight();
        int totalTextHeight = lines.size() * lineHeight;

        // 计算文案区域的起始 Y 坐标（距离底部 50 像素）
        int sloganTextY = mainImage.getHeight() - 50 - totalTextHeight;

        // 绘制鸡汤文案背景（覆盖所有行）
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

        // 绘制鸡汤文案（多行）
        g2d.setColor(Color.BLACK); // 重新设置文字颜色
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineWidth = fmSlogan.stringWidth(line);
            int lineX = (mainImage.getWidth() - lineWidth) / 2; // 每行水平居中
            g2d.drawString(line, lineX, sloganTextY + i * lineHeight);
        }

        // 释放 Graphics2D 资源
        g2d.dispose();

        // 将处理后的图片写入输出流并返回
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(mainImage, "jpg", baos);
        return baos;
    }

    // 获取当前时间的方法
    private static String getCurrentTime() {
        // 使用完整星期格式(EEEE)并指定中文区域
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
        return sdf.format(new Date());
    }

    // 自动换行方法
    private static List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        java.util.List<String> lines = new ArrayList<>();
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
