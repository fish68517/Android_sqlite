package com.localmusic.player.util;

import androidx.annotation.NonNull;

import com.localmusic.player.model.LyricLine;
import com.localmusic.player.model.Song;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LocalLyricParser {

    private static final Pattern TIMESTAMP_PATTERN =
            Pattern.compile("\\[(\\d{1,2}):(\\d{1,2})(?:[.:](\\d{1,3}))?]");

    private LocalLyricParser() {
    }

    @NonNull
    public static List<LyricLine> parse(@NonNull Song song) {
        File lyricFile = findLyricFile(song);
        if (lyricFile == null || !lyricFile.isFile()) {
            return Collections.emptyList();
        }

        List<LyricLine> lines = parseWithCharset(lyricFile, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            lines = parseWithCharset(lyricFile, Charset.forName("GBK"));
        }

        lines.sort(Comparator.comparingLong(LyricLine::getTimestampMs));
        return lines;
    }

    private static List<LyricLine> parseWithCharset(File lyricFile, Charset charset) {
        List<LyricLine> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(lyricFile), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line, lines);
            }
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
        return lines;
    }

    private static void parseLine(String rawLine, List<LyricLine> lines) {
        if (rawLine == null || rawLine.trim().isEmpty()) {
            return;
        }

        Matcher matcher = TIMESTAMP_PATTERN.matcher(rawLine);
        List<Long> timestamps = new ArrayList<>();
        int contentStart = 0;
        while (matcher.find()) {
            timestamps.add(parseTimestamp(matcher.group(1), matcher.group(2), matcher.group(3)));
            contentStart = matcher.end();
        }

        if (timestamps.isEmpty()) {
            return;
        }

        String text = rawLine.substring(Math.min(contentStart, rawLine.length())).trim();
        if (text.isEmpty()) {
            text = "…";
        }

        for (Long timestamp : timestamps) {
            lines.add(new LyricLine(timestamp, text));
        }
    }

    private static long parseTimestamp(String minutePart, String secondPart, String fractionPart) {
        long minutes = parseLong(minutePart);
        long seconds = parseLong(secondPart);
        long fraction = parseLong(fractionPart);

        if (fractionPart != null) {
            if (fractionPart.length() == 1) {
                fraction *= 100L;
            } else if (fractionPart.length() == 2) {
                fraction *= 10L;
            }
        }

        return minutes * 60_000L + seconds * 1_000L + fraction;
    }

    private static long parseLong(String source) {
        if (source == null || source.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(source.trim());
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }

    private static File findLyricFile(Song song) {
        String filePath = song.getFilePath();
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }

        File audioFile = new File(filePath);
        File parentFile = audioFile.getParentFile();
        if (parentFile == null) {
            return null;
        }

        String fileName = audioFile.getName();
        int extensionIndex = fileName.lastIndexOf('.');
        String baseName = extensionIndex >= 0 ? fileName.substring(0, extensionIndex) : fileName;

        Set<File> candidates = new LinkedHashSet<>();
        candidates.add(new File(parentFile, baseName + ".lrc"));
        candidates.add(new File(parentFile, song.getTitle() + ".lrc"));
        candidates.add(new File(parentFile, song.getArtist() + " - " + song.getTitle() + ".lrc"));

        for (File candidate : candidates) {
            if (candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }
}
