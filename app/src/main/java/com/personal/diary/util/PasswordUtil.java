package com.personal.diary.util;

import java.security.MessageDigest;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] data = digest.digest(raw.getBytes("UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (byte b : data) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Password hash failed", e);
        }
    }
}
