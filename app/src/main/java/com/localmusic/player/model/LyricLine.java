package com.localmusic.player.model;

public class LyricLine {

    private final long timestampMs;
    private final String text;

    public LyricLine(long timestampMs, String text) {
        this.timestampMs = timestampMs;
        this.text = text;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public String getText() {
        return text;
    }
}
