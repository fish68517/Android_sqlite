package com.example.booktracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(Date date){
        return simpleDateFormat.format(date);
    }
    
    /**
     * 获取当前日期时间的格式化字符串
     */
    public static String getNowDate(){
        return format(new Date());
    }
}
