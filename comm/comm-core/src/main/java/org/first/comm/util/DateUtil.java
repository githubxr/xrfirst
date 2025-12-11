package org.first.comm.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类（基于 Java 8+ 的 LocalDateTime，线程安全，无时区问题）
 * 核心功能：返回格式为 "yyyy-MM-dd HH:mm:ss" 的当前时间字符串
 */
public class DateUtil {

    // 定义全局的日期时间格式化器（线程安全，可复用）
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前时间的字符串，格式：yyyy-MM-dd HH:mm:ss
     * @return 格式化后的当前时间字符串，例如：2025-12-10 21:27:03
     */
    public static String getCurrentDateTimeStr() {
        // 获取系统当前时间（默认使用系统时区）
        LocalDateTime now = LocalDateTime.now();
        // 按照指定格式格式化并返回
        return now.format(DEFAULT_DATETIME_FORMATTER);
    }

    // ========== 以下是扩展方法（可选，方便后续使用） ==========

    /**
     * 将 LocalDateTime 转换为指定格式的字符串
     * @param localDateTime 要格式化的时间对象
     * @param formatter 格式化器
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        if (localDateTime == null) {
            return ""; // 或根据业务返回 null
        }
        return localDateTime.format(formatter);
    }

    /**
     * 将字符串解析为 LocalDateTime（格式：yyyy-MM-dd HH:mm:ss）
     * @param dateTimeStr 时间字符串
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
    }

    // 私有化构造方法，禁止实例化工具类
    private DateUtil() {}
}