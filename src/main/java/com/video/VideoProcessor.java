//package com.video;
//
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Random;
//
//public class VideoProcessor {
//    public static void main(String[] args) {
//        // 输入视频文件路径
//        String inputFile = "C:\\Users\\仁联集团\\Downloads\\video3333.mp4";
//        // 输出文件夹
//        String outputDir = "output_videos";
//        Path outputPath = Paths.get(outputDir);
//
//        // 创建输出文件夹
//        try {
//            if (!Files.exists(outputPath)) {
//                Files.createDirectories(outputPath);
//            }
//        } catch (IOException e) {
//            System.err.println("创建输出文件夹失败: " + e.getMessage());
//            return;
//        }
//
//        // 1. 分割视频为每10分钟一个片段
//        String segmentCommand = String.format(
//                "ffmpeg -i \"%s\" -c copy -map 0 -segment_time 30 -f segment \"%s\\part%%d.mp4\"",
//                inputFile, outputDir
//        );
//        executeCommand(segmentCommand);
//        System.out.println("视频分割完成！");
//
//        // 2. 对每个分割的视频进行随机调整
//        Random random = new Random();
//        try {
//            Files.list(outputPath)
//                    .filter(p -> p.getFileName().toString().startsWith("part") && p.getFileName().toString().endsWith(".mp4"))
//                    .forEach(p -> {
//                        String inputSegment = p.toString();
//                        String outputSegment = outputPath.resolve("调整之后的_" + p.getFileName()).toString();
//
//                        // 随机调整参数
//                        double temperature = random.nextDouble() * 6000 + 4000; // 4000K 到 10000K
//                        double hue = random.nextDouble() * 60 - 30;            // -30 到 30 度
//                        double saturation = random.nextDouble() + 0.5;   // 0.5 到 1.5
//                        double brightness = random.nextDouble() * 0.6 - 0.3;   // -0.3 到 0.3
//                        double contrast = random.nextDouble() * 0.6 + 0.7;     // 0.7 到 1.3
//
//                        // 构造滤镜命令
//                        String filters = String.format(
//                                "colortemperature=temperature=%.0f,hue=h=%.2f,eq=brightness=%.2f:contrast=%.2f:saturation=%.2f",
//                                temperature, hue, brightness, contrast, saturation
//                        );
//
//                        // 执行调整命令
//                        String adjustCommand = String.format(
//                                "ffmpeg -i \"%s\" -vf \"%s\" -c:a copy \"%s\"",
//                                inputSegment, filters, outputSegment
//                        );
//                        executeCommand(adjustCommand);
//                        System.out.printf("已调整 %s：色温=%.0fK, 色调=%.2f度, 亮度=%.2f, 对比度=%.2f, 饱和度=%.2f%n",
//                                p.getFileName(), temperature, hue, brightness, contrast, saturation);
//                    });
//        } catch (IOException e) {
//            System.err.println("处理文件列表时出错: " + e.getMessage());
//        }
//
//        System.out.println("所有操作完成！");
//    }
//
//    private static void executeCommand(String command) {
//        try {
//            Process process = Runtime.getRuntime().exec(command);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line); // 输出 FFmpeg 的日志
//            }
//            process.waitFor();
//            if (process.exitValue() != 0) {
//                throw new RuntimeException("命令执行失败");
//            }
//        } catch (IOException | InterruptedException e) {
//            System.err.println("执行命令时出错: " + e.getMessage());
//        }
//    }
//}
