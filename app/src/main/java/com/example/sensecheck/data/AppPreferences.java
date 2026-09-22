package com.example.sensecheck.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.UUID;

public final class AppPreferences {
    private static final String FILE_NAME = "sense_check_preferences";
    private static final String KEY_STUDENT_NO = "student_no";
    private static final String KEY_NAME = "name";
    private static final String KEY_CLASS_NAME = "class_name";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_COURSE_NAME = "course_name";
    private static final String KEY_WEEKDAY = "weekday";
    private static final String KEY_START_MINUTES = "start_minutes";
    private static final String KEY_END_MINUTES = "end_minutes";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_RADIUS = "radius";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_AUTO_ENABLED = "auto_enabled";

    private final SharedPreferences preferences;

    public AppPreferences(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public String getStudentNo() {
        return preferences.getString(KEY_STUDENT_NO, "");
    }

    public String getName() {
        return preferences.getString(KEY_NAME, "");
    }

    public String getClassName() {
        return preferences.getString(KEY_CLASS_NAME, "");
    }

    public void saveProfile(String studentNo, String name, String className) {
        preferences.edit()
                .putString(KEY_STUDENT_NO, studentNo.trim())
                .putString(KEY_NAME, name.trim())
                .putString(KEY_CLASS_NAME, className.trim())
                .apply();
    }

    public String getDeviceId() {
        String value = preferences.getString(KEY_DEVICE_ID, "");
        if (value == null || value.isEmpty()) {
            value = UUID.randomUUID().toString();
            preferences.edit().putString(KEY_DEVICE_ID, value).apply();
        }
        return value;
    }

    public boolean isProfileConfigured() {
        return !getStudentNo().isEmpty() && !getName().isEmpty();
    }

    public void saveCourse(String courseName,
                           int weekday,
                           int startMinutes,
                           int endMinutes,
                           double latitude,
                           double longitude,
                           float radiusMeters,
                           int intervalMinutes,
                           boolean autoEnabled) {
        preferences.edit()
                .putString(KEY_COURSE_NAME, courseName.trim())
                .putInt(KEY_WEEKDAY, weekday)
                .putInt(KEY_START_MINUTES, startMinutes)
                .putInt(KEY_END_MINUTES, endMinutes)
                .putString(KEY_LATITUDE, Double.toString(latitude))
                .putString(KEY_LONGITUDE, Double.toString(longitude))
                .putFloat(KEY_RADIUS, radiusMeters)
                .putInt(KEY_INTERVAL, intervalMinutes)
                .putBoolean(KEY_AUTO_ENABLED, autoEnabled)
                .apply();
    }

    public String getCourseName() {
        return preferences.getString(KEY_COURSE_NAME, "");
    }

    public int getWeekday() {
        return preferences.getInt(KEY_WEEKDAY, Calendar.MONDAY);
    }

    public int getStartMinutes() {
        return preferences.getInt(KEY_START_MINUTES, 8 * 60);
    }

    public int getEndMinutes() {
        return preferences.getInt(KEY_END_MINUTES, 9 * 60 + 40);
    }

    public double getLatitude() {
        return readDouble(KEY_LATITUDE);
    }

    public double getLongitude() {
        return readDouble(KEY_LONGITUDE);
    }

    public float getRadiusMeters() {
        return preferences.getFloat(KEY_RADIUS, 150f);
    }

    public int getIntervalMinutes() {
        return preferences.getInt(KEY_INTERVAL, 15);
    }

    public boolean isAutoEnabled() {
        return preferences.getBoolean(KEY_AUTO_ENABLED, true);
    }

    public boolean isCourseConfigured() {
        return !getCourseName().isEmpty()
                && !Double.isNaN(getLatitude())
                && !Double.isNaN(getLongitude());
    }

    private double readDouble(String key) {
        String value = preferences.getString(key, "");
        if (value == null || value.isEmpty()) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return Double.NaN;
        }
    }
}

