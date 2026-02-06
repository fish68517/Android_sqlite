package com.example.healthdietapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * DateUtils - Utility class for date operations
 */
public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    /**
     * Get current date in YYYY-MM-DD format
     */
    public static String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    /**
     * Get date string in YYYY-MM-DD format
     */
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Get display date string (e.g., "Jan 01, 2024")
     */
    public static String getDisplayDate(String dateString) {
        try {
            Date date = dateFormat.parse(dateString);
            return displayFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Add days to a date string
     */
    public static String addDays(String dateString, int days) {
        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(String dateString) {
        return dateString.equals(getCurrentDate());
    }
}
