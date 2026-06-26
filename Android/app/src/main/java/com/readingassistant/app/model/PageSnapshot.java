package com.readingassistant.app.model;

public class PageSnapshot {
    public static final String SOURCE_ACCESSIBILITY_TEXT = "ACCESSIBILITY_TEXT";
    public static final String SOURCE_SCREENSHOT_IMAGE = "SCREENSHOT_IMAGE";

    private final String packageName;
    private final String sourceType;
    private final String text;
    private final String contentHash;
    private final String preview;
    private final long capturedAt;

    public PageSnapshot(String packageName, String sourceType, String text, String contentHash, String preview, long capturedAt) {
        this.packageName = packageName == null ? "" : packageName;
        this.sourceType = sourceType;
        this.text = text == null ? "" : text;
        this.contentHash = contentHash == null ? "" : contentHash;
        this.preview = preview == null ? "" : preview;
        this.capturedAt = capturedAt;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getText() {
        return text;
    }

    public String getContentHash() {
        return contentHash;
    }

    public String getPreview() {
        return preview;
    }

    public long getCapturedAt() {
        return capturedAt;
    }

    public boolean hasUsefulText() {
        int chineseOrLetterCount = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c) || (c >= 0x4E00 && c <= 0x9FFF)) {
                chineseOrLetterCount++;
            }
        }
        return chineseOrLetterCount >= 50;
    }
}
