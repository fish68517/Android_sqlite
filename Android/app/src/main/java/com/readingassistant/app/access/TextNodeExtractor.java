package com.readingassistant.app.access;

import android.view.accessibility.AccessibilityNodeInfo;

import com.readingassistant.app.model.PageSnapshot;
import com.readingassistant.app.scheduler.PageFingerprint;
import com.readingassistant.app.util.DebugLog;

import java.util.LinkedHashSet;
import java.util.Set;

public final class TextNodeExtractor {
    private static final int MAX_TEXT_LENGTH = 8000;

    private TextNodeExtractor() {
    }

    public static PageSnapshot fromRoot(AccessibilityNodeInfo root, CharSequence packageName) {
        Set<String> lines = new LinkedHashSet<>();
        collect(root, lines);
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            if (builder.length() + line.length() > MAX_TEXT_LENGTH) {
                break;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line);
        }
        String text = builder.toString().trim();
        String hash = PageFingerprint.hash(text);
        String preview = text.length() > 30 ? text.substring(0, 30) : text;
        DebugLog.d("TextNodeExtractor result package=" + packageName
                + " lines=" + lines.size()
                + " textLength=" + text.length()
                + " hash=" + DebugLog.shortHash(hash)
                + " preview=" + DebugLog.preview(text));
        return new PageSnapshot(
                packageName == null ? "" : packageName.toString(),
                PageSnapshot.SOURCE_ACCESSIBILITY_TEXT,
                text,
                hash,
                preview,
                System.currentTimeMillis()
        );
    }

    private static void collect(AccessibilityNodeInfo node, Set<String> lines) {
        if (node == null || lines.size() > 300) {
            return;
        }
        if (node.isVisibleToUser()) {
            addText(lines, node.getText());
            if (node.getText() == null) {
                addText(lines, node.getContentDescription());
            }
        }
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                collect(child, lines);
                child.recycle();
            }
        }
    }

    private static void addText(Set<String> lines, CharSequence raw) {
        if (raw == null) {
            return;
        }
        String text = raw.toString()
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
        if (shouldKeep(text)) {
            lines.add(text);
        }
    }

    private static boolean shouldKeep(String text) {
        if (text == null || text.length() < 2) {
            return false;
        }
        if (text.matches("^[0-9:：/\\-. ]+$")) {
            return false;
        }
        String lower = text.toLowerCase();
        return !lower.equals("返回")
                && !lower.equals("更多")
                && !lower.equals("搜索")
                && !lower.equals("设置")
                && !lower.equals("分享")
                && !lower.equals("关闭");
    }
}
