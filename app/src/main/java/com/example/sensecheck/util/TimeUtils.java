package com.example.sensecheck.util;

import com.example.sensecheck.data.AppPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private TimeUtils() {
    }

    public static boolean isInCourseWindow(AppPreferences preferences, long timestamp) {
        if (!preferences.isCourseConfigured()) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        if (calendar.get(Calendar.DAY_OF_WEEK) != preferences.getWeekday()) {
            return false;
        }
        int nowMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        return nowMinutes >= preferences.getStartMinutes()
                && nowMinutes <= preferences.getEndMinutes();
    }

    public static long nextCourseStart(AppPreferences preferences, long now) {
        Calendar candidate = Calendar.getInstance();
        candidate.setTimeInMillis(now);
        candidate.set(Calendar.HOUR_OF_DAY, preferences.getStartMinutes() / 60);
        candidate.set(Calendar.MINUTE, preferences.getStartMinutes() % 60);
        candidate.set(Calendar.SECOND, 0);
        candidate.set(Calendar.MILLISECOND, 0);

        int currentDay = candidate.get(Calendar.DAY_OF_WEEK);
        int daysAhead = (preferences.getWeekday() - currentDay + 7) % 7;
        candidate.add(Calendar.DAY_OF_YEAR, daysAhead);
        if (candidate.getTimeInMillis() <= now) {
            candidate.add(Calendar.DAY_OF_YEAR, 7);
        }
        return candidate.getTimeInMillis();
    }

    public static String formatDateTime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date(timestamp));
    }

    public static String formatMinutes(int minutes) {
        return String.format(Locale.CHINA, "%02d:%02d", minutes / 60, minutes % 60);
    }

    public static String weekdayName(int weekday) {
        switch (weekday) {
            case Calendar.MONDAY:
                return "星期一";
            case Calendar.TUESDAY:
                return "星期二";
            case Calendar.WEDNESDAY:
                return "星期三";
            case Calendar.THURSDAY:
                return "星期四";
            case Calendar.FRIDAY:
                return "星期五";
            case Calendar.SATURDAY:
                return "星期六";
            default:
                return "星期日";
        }
    }
}

