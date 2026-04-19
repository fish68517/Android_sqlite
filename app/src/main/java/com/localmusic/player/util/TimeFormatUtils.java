package com.localmusic.player.util;

import java.util.Locale;

public final class TimeFormatUtils {

    private TimeFormatUtils() {
    }

    public static String formatDuration(int durationMs) {
        long totalSeconds = Math.max(durationMs, 0) / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
