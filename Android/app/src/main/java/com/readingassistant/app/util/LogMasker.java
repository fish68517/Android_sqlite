package com.readingassistant.app.util;

public final class LogMasker {
    private LogMasker() {
    }

    public static String maskSecret(String value) {
        if (value == null || value.length() <= 8) {
            return "***";
        }
        return value.substring(0, 3) + "***" + value.substring(value.length() - 3);
    }
}
