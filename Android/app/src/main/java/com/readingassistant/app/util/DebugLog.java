package com.readingassistant.app.util;

import android.util.Log;

import com.readingassistant.app.BuildConfig;

public final class DebugLog {
    public static final String TAG = "ReadingAssistant";

    private DebugLog() {
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void w(String message) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, message);
        }
    }

    public static void e(String message, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, throwable);
        }
    }

    public static String preview(String value) {
        if (value == null) {
            return "";
        }
        String clean = value.replaceAll("\\s+", " ").trim();
        if (clean.length() <= 80) {
            return clean;
        }
        return clean.substring(0, 80) + "...";
    }

    public static String shortHash(String hash) {
        if (hash == null || hash.length() <= 10) {
            return hash == null ? "" : hash;
        }
        return hash.substring(0, 10);
    }
}
