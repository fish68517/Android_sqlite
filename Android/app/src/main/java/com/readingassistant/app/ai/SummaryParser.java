package com.readingassistant.app.ai;

public final class SummaryParser {
    public static final String INSUFFICIENT_CONTENT_SUMMARY = "总结：当前页面内容不足，暂无法总结。";

    private SummaryParser() {
    }

    public static String normalize(String raw, String fallbackPreview, int maxChars) {
        String value = raw == null ? "" : raw.trim();
        value = value.replace("```", "")
                .replaceAll("(?i)^assistant\\s*[:：]", "")
                .replaceAll("\\s+", " ")
                .trim();
        if (value.isEmpty()) {
            value = sanitizeFallbackPreview(fallbackPreview);
        }
        if (value.startsWith("总结:")) {
            value = "总结：" + value.substring("总结:".length()).trim();
        }
        if (!value.startsWith("总结：")) {
            value = "总结：" + value;
        }
        String content = value.substring("总结：".length()).trim();
        if (content.length() > maxChars) {
            content = content.substring(0, maxChars).trim() + "...";
        }
        return "总结：" + content;
    }

    private static String sanitizeFallbackPreview(String fallbackPreview) {
        String preview = fallbackPreview == null ? "" : fallbackPreview.trim();
        preview = preview.replaceAll("\\s+", " ");
        if (preview.startsWith("总结：")) {
            preview = preview.substring("总结：".length()).trim();
        } else if (preview.startsWith("总结:")) {
            preview = preview.substring("总结:".length()).trim();
        }
        if (!preview.isEmpty()) {
            return preview;
        }
        return INSUFFICIENT_CONTENT_SUMMARY.substring("总结：".length());
    }

    public static boolean isInsufficient(String summary) {
        return summary != null && summary.contains("当前页面内容不足");
    }
}
