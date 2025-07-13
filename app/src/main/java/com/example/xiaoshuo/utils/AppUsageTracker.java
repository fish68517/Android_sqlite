package com.example.xiaoshuo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用于跟踪应用使用时长的工具类
 */
public class AppUsageTracker {
    private static final String TAG = "AppUsageTracker";
    private static final String PREF_NAME = "app_usage_prefs";
    private static final String KEY_TOTAL_TIME = "total_usage_time"; // 总使用时间（毫秒）
    private static final String KEY_LAST_SESSION_START = "last_session_start"; // 最近一次会话开始时间
    private static final String KEY_TODAY_DATE = "today_date"; // 今天的日期
    private static final String KEY_TODAY_USAGE = "today_usage"; // 今天的使用时间
    
    private static AppUsageTracker instance;
    private SharedPreferences preferences;
    
    // 各页面使用时间记录
    private Map<String, Long> pageStartTimes = new HashMap<>();
    private Map<String, Long> pageDurations = new HashMap<>();
    
    private AppUsageTracker(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized AppUsageTracker getInstance(Context context) {
        if (instance == null) {
            instance = new AppUsageTracker(context);
        }
        return instance;
    }
    
    /**
     * 应用启动时调用
     */
    public void onAppStart() {
        long currentTime = System.currentTimeMillis();
        preferences.edit().putLong(KEY_LAST_SESSION_START, currentTime).apply();
        
        // 检查是否是新的一天，如果是则重置今日使用时间
        String today = java.time.LocalDate.now().toString();
        String storedDate = preferences.getString(KEY_TODAY_DATE, "");
        
        if (!today.equals(storedDate)) {
            preferences.edit()
                    .putString(KEY_TODAY_DATE, today)
                    .putLong(KEY_TODAY_USAGE, 0)
                    .apply();
        }
        
        Log.d(TAG, "App session started");
    }
    
    /**
     * 应用暂停或关闭时调用
     */
    public void onAppPause() {
        long sessionStart = preferences.getLong(KEY_LAST_SESSION_START, 0);
        if (sessionStart > 0) {
            long currentTime = System.currentTimeMillis();
            long sessionDuration = currentTime - sessionStart;
            
            // 更新总使用时间
            long totalTime = preferences.getLong(KEY_TOTAL_TIME, 0);
            totalTime += sessionDuration;
            
            // 更新今日使用时间
            long todayUsage = preferences.getLong(KEY_TODAY_USAGE, 0);
            todayUsage += sessionDuration;
            
            preferences.edit()
                    .putLong(KEY_TOTAL_TIME, totalTime)
                    .putLong(KEY_TODAY_USAGE, todayUsage)
                    .putLong(KEY_LAST_SESSION_START, 0) // 重置会话开始时间
                    .apply();
            
            Log.d(TAG, "Session ended, duration: " + formatDuration(sessionDuration));
        }
    }
    
    /**
     * 记录页面访问开始时间
     * @param pageName 页面名称标识
     */
    public void trackPageStart(String pageName) {
        pageStartTimes.put(pageName, System.currentTimeMillis());
    }
    
    /**
     * 记录页面访问结束，并计算停留时间
     * @param pageName 页面名称标识
     */
    public void trackPageEnd(String pageName) {
        Long startTime = pageStartTimes.remove(pageName);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            Long currentDuration = pageDurations.getOrDefault(pageName, 0L);
            pageDurations.put(pageName, currentDuration + duration);
            Log.d(TAG, "Page " + pageName + " visited for " + formatDuration(duration));
        }
    }
    
    /**
     * 获取总使用时长（格式化为可读字符串）
     */
    public String getTotalUsageTime() {
        long totalTime = preferences.getLong(KEY_TOTAL_TIME, 0);
        return formatDuration(totalTime);
    }
    
    /**
     * 获取今日使用时长（格式化为可读字符串）
     */
    public String getTodayUsageTime() {
        long todayUsage = preferences.getLong(KEY_TODAY_USAGE, 0);
        return formatDuration(todayUsage);
    }
    
    /**
     * 获取当前会话时长
     */
    public String getCurrentSessionTime() {
        long sessionStart = preferences.getLong(KEY_LAST_SESSION_START, 0);
        if (sessionStart > 0) {
            long currentDuration = System.currentTimeMillis() - sessionStart;
            return formatDuration(currentDuration);
        }
        return "0分钟";
    }
    
    /**
     * 获取特定页面的访问时长
     */
    public String getPageDuration(String pageName) {
        Long duration = pageDurations.getOrDefault(pageName, 0L);
        return formatDuration(duration);
    }
    
    /**
     * 格式化时长为可读格式
     */
    private String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        
        if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        } else if (minutes > 0) {
            return minutes + "分钟";
        } else {
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
            return seconds + "秒";
        }
    }
    
    /**
     * 获取原始总使用时长（毫秒）
     */
    public long getRawTotalUsageTime() {
        return preferences.getLong(KEY_TOTAL_TIME, 0);
    }
    
    /**
     * 获取原始今日使用时长（毫秒）
     */
    public long getRawTodayUsageTime() {
        return preferences.getLong(KEY_TODAY_USAGE, 0);
    }
} 