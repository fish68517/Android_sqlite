package com.readingassistant.app.scheduler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PageFingerprint {
    private PageFingerprint() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("\\s+", "")
                .replaceAll("[\\p{Punct}，。！？；：“”‘’（）【】《》、]", "")
                .trim();
    }

    public static String hash(String text) {
        String normalized = normalize(text);
        if (normalized.isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(normalized.hashCode());
        }
    }
}
