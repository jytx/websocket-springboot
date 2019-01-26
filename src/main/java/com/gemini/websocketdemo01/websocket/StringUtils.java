package com.gemini.websocketdemo01.websocket;

/**
 * string工具类
 * @author LiQun
 * @date 2019/1/25
 */
public class StringUtils {
    /**
     * 判断指定字符串是否不等于null和空字符串
     *
     * @param str 指定字符串
     * @return 如果不等于null和空字符串则返回true，否则返回false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断指定字符串是否等于null或空字符串
     *
     * @param str 指定字符串
     * @return 如果等于null或空字符串则返回true，否则返回false
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim()) || str == "null";
    }
}
