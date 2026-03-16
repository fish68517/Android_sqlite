package com.hakimi.utils;

/**
 * 文本工具类
 * 
 * @author hakimi
 */
public class TextUtils {

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }
}

