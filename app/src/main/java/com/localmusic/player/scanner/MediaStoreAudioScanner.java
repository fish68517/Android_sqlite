package com.localmusic.player.scanner;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.localmusic.player.R;
import com.localmusic.player.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MediaStoreAudioScanner {

    private static final long MIN_DURATION_MS = 10_000L;

    public List<Song> scan(Context context) {
        List<Song> songs = new ArrayList<>();
        Set<String> seenPaths = new HashSet<>();
        ContentResolver contentResolver = context.getContentResolver();
        String unknownSong = context.getString(R.string.song_unknown_title);
        String unknownArtist = context.getString(R.string.song_unknown_artist);
        String unknownAlbum = context.getString(R.string.song_unknown_album);

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND "
                + MediaStore.Audio.Media.DURATION + ">= ?";
        String[] selectionArgs = new String[]{String.valueOf(MIN_DURATION_MS)};
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);

        if (cursor == null) {
            return songs;
        }

        try {
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
            int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                String filePath = safeString(cursor.getString(pathIndex));
                if (!filePath.isEmpty() && !seenPaths.add(filePath)) {
                    continue;
                }

                String title = normalizeText(cursor.getString(titleIndex), unknownSong);
                String artist = normalizeText(cursor.getString(artistIndex), unknownArtist);
                String album = normalizeText(cursor.getString(albumIndex), unknownAlbum);
                long duration = cursor.getLong(durationIndex);
                long dateAdded = cursor.getLong(dateAddedIndex);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                songs.add(new Song(
                        id,
                        title,
                        artist,
                        album,
                        duration,
                        filePath,
                        resolveFolderName(context, filePath),
                        dateAdded,
                        contentUri));
            }
        } finally {
            cursor.close();
        }

        return songs;
    }

    private String resolveFolderName(Context context, String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return context.getString(R.string.song_unknown_folder);
        }

        File parentFile = new File(filePath).getParentFile();
        if (parentFile == null) {
            return context.getString(R.string.song_unknown_folder);
        }

        String folder = parentFile.getName();
        return folder == null || folder.trim().isEmpty()
                ? context.getString(R.string.song_unknown_folder)
                : folder;
    }

    private String normalizeText(String source, String fallback) {
        String value = safeString(source);
        if (value.isEmpty()) {
            return fallback;
        }

        String lowerCase = value.toLowerCase(Locale.ROOT);
        if ("<unknown>".equals(lowerCase) || "unknown".equals(lowerCase)) {
            return fallback;
        }

        return value;
    }

    private String safeString(String value) {
        return value == null ? "" : value.trim();
    }
}
