package com.project.demo.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyUtil {

    public static String getDateyyyy_MM_dd(String inputDate) {

        // 定义输入和输出的格式
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 解析输入字符串为 LocalDate
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);

        // 格式化为目标格式
        String formattedDate = date.format(outputFormatter);
        System.out.println(inputDate);
        System.out.println("---->");
        System.out.println(formattedDate);
        return formattedDate;
    }

    // 假设这是一部分中国的法定节假日（可以根据需要补充）
    private static final Set<LocalDate> holidays = new HashSet<>();

    static {
        // 添加所有已知的节假日
        holidays.add(LocalDate.of(2024, 1, 1));  // 新年
        holidays.add(LocalDate.of(2024, 2, 10)); // 春节假期
        holidays.add(LocalDate.of(2024, 2, 11)); // 春节假期
        holidays.add(LocalDate.of(2024, 2, 12)); // 春节假期
        holidays.add(LocalDate.of(2024, 2, 13)); // 春节假期
        holidays.add(LocalDate.of(2024, 2, 14)); // 春节假期
        holidays.add(LocalDate.of(2024, 2, 15)); // 春节假期
        holidays.add(LocalDate.of(2024, 2, 16)); // 春节假期
        holidays.add(LocalDate.of(2024, 4, 4));  // 清明节
        holidays.add(LocalDate.of(2024, 4, 5));  // 清明节
        holidays.add(LocalDate.of(2024, 5, 1));  // 劳动节
        holidays.add(LocalDate.of(2024, 5, 2));  // 劳动节
        holidays.add(LocalDate.of(2024, 5, 3));  // 劳动节
        holidays.add(LocalDate.of(2024, 6, 10));  // 端午节
        holidays.add(LocalDate.of(2024, 9, 13)); // 中秋节
        holidays.add(LocalDate.of(2024, 10, 1)); // 国庆节
        holidays.add(LocalDate.of(2024, 10, 2)); // 国庆节
        holidays.add(LocalDate.of(2024, 10, 3)); // 国庆节
        holidays.add(LocalDate.of(2024, 10, 4)); // 国庆节
        holidays.add(LocalDate.of(2024, 10, 7)); // 国庆节
    }

    /**
     * 计算两个给定日期之间的所有工作日（周一至周五）
     *
     * @param startDateStr 起始日期
     * @param endDateStr   结束日期
     * @return 工作日列表
     */
    public static List<String> calculateWorkdays(String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        List<String> workdays = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (isWeekday(currentDate) && isWorkday(currentDate)) {
                workdays.add(currentDate.format(formatter));
            }
            currentDate = currentDate.plusDays(1);
        }

        return workdays;
    }

    public static boolean isWorkday(LocalDate date) {
        // 判断是否是周末
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // 判断是否是节假日
        return !holidays.contains(date);

        // 如果既不是周末也不是节假日，那么是工作日
    }

    /**
     * 判断给定的日期是否是工作日（周一到周五）
     *
     * @param date 字符串格式的日期
     * @return true表示是工作日，false表示不是工作日
     */
    private static boolean isWeekday(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 1 && date.getDayOfWeek().getValue() <= 5;
    }

}
