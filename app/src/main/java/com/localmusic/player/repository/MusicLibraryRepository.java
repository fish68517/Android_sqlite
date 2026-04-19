package com.localmusic.player.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.localmusic.player.R;
import com.localmusic.player.model.LibraryCategory;
import com.localmusic.player.model.LibraryItem;
import com.localmusic.player.model.Song;
import com.localmusic.player.scanner.MediaStoreAudioScanner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicLibraryRepository {

    public interface Listener {
        void onMusicLibraryChanged();
    }

    private static volatile MusicLibraryRepository instance;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Set<Listener> listeners = new CopyOnWriteArraySet<>();
    private final MediaStoreAudioScanner audioScanner = new MediaStoreAudioScanner();
    private final List<Song> songs = new ArrayList<>();

    private Context applicationContext;
    private boolean loading;

    private MusicLibraryRepository() {
    }

    public static MusicLibraryRepository getInstance() {
        if (instance == null) {
            synchronized (MusicLibraryRepository.class) {
                if (instance == null) {
                    instance = new MusicLibraryRepository();
                }
            }
        }
        return instance;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void scan(Context context, boolean force) {
        synchronized (this) {
            if (loading) {
                return;
            }
            if (!force && !songs.isEmpty()) {
                notifyListeners();
                return;
            }
            loading = true;
        }

        notifyListeners();
        Context appContext = context.getApplicationContext();
        applicationContext = appContext;
        executorService.execute(() -> {
            List<Song> scannedSongs = audioScanner.scan(appContext);
            synchronized (MusicLibraryRepository.this) {
                songs.clear();
                songs.addAll(scannedSongs);
                loading = false;
            }
            notifyListeners();
        });
    }

    public synchronized boolean isLoading() {
        return loading;
    }

    public synchronized int getSongCount() {
        return songs.size();
    }

    public synchronized int getArtistCount() {
        Set<String> artists = new LinkedHashSet<>();
        for (Song song : songs) {
            artists.add(song.getArtist());
        }
        return artists.size();
    }

    public synchronized int getAlbumCount() {
        Set<String> albums = new LinkedHashSet<>();
        for (Song song : songs) {
            albums.add(song.getAlbum());
        }
        return albums.size();
    }

    public synchronized int getFolderCount() {
        Set<String> folders = new LinkedHashSet<>();
        for (Song song : songs) {
            folders.add(song.getFolderName());
        }
        return folders.size();
    }

    public synchronized List<LibraryItem> getLibraryItems(@NonNull LibraryCategory category, @NonNull String query) {
        if (category == LibraryCategory.ARTISTS) {
            return buildAggregateItems(category, query, AggregateType.ARTIST);
        }
        if (category == LibraryCategory.ALBUMS) {
            return buildAggregateItems(category, query, AggregateType.ALBUM);
        }
        if (category == LibraryCategory.FOLDERS) {
            return buildAggregateItems(category, query, AggregateType.FOLDER);
        }
        return buildSongItems(query);
    }

    public synchronized List<Song> getSongs(@NonNull String query) {
        String normalizedQuery = normalizeQuery(query);
        List<Song> result = new ArrayList<>();
        for (Song song : songs) {
            if (!normalizedQuery.isEmpty()
                    && !contains(song.getTitle(), normalizedQuery)
                    && !contains(song.getArtist(), normalizedQuery)
                    && !contains(song.getAlbum(), normalizedQuery)
                    && !contains(song.getFolderName(), normalizedQuery)) {
                continue;
            }
            result.add(song);
        }
        return result;
    }

    public synchronized Song getSongById(long songId) {
        for (Song song : songs) {
            if (song.getId() == songId) {
                return song;
            }
        }
        return null;
    }

    public synchronized List<Song> getSongsByIds(@NonNull List<Long> songIds) {
        List<Song> result = new ArrayList<>();
        for (Long songId : songIds) {
            if (songId == null) {
                continue;
            }
            Song song = getSongById(songId);
            if (song != null) {
                result.add(song);
            }
        }
        return result;
    }

    public synchronized String buildLibrarySummary() {
        if (applicationContext == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(applicationContext.getString(
                R.string.ai_library_summary,
                getSongCount(),
                getArtistCount(),
                getAlbumCount(),
                getFolderCount()));

        int previewCount = Math.min(songs.size(), 12);
        if (previewCount > 0) {
            builder.append('\n')
                    .append(applicationContext.getString(R.string.ai_library_preview_title));
            for (int i = 0; i < previewCount; i++) {
                Song song = songs.get(i);
                builder.append('\n')
                        .append(i + 1)
                        .append(". ")
                        .append(song.getTitle())
                        .append(" - ")
                        .append(song.getArtist())
                        .append(" / ")
                        .append(song.getAlbum());
            }
        }

        return builder.toString();
    }

    private List<LibraryItem> buildSongItems(String query) {
        List<LibraryItem> items = new ArrayList<>();
        for (Song song : getSongs(query)) {
            items.add(new LibraryItem(
                    LibraryCategory.SONGS,
                    song.getTitle(),
                    song.getArtist() + " / " + song.getAlbum(),
                    formatDuration(song.getDuration()),
                    song));
        }
        return items;
    }

    private List<LibraryItem> buildAggregateItems(LibraryCategory category, String query, AggregateType aggregateType) {
        String normalizedQuery = normalizeQuery(query);
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (Song song : songs) {
            String key;
            if (aggregateType == AggregateType.ARTIST) {
                key = song.getArtist();
            } else if (aggregateType == AggregateType.ALBUM) {
                key = song.getAlbum();
            } else {
                key = song.getFolderName();
            }

            if (!normalizedQuery.isEmpty() && !contains(key, normalizedQuery)) {
                continue;
            }

            Integer count = counts.get(key);
            counts.put(key, count == null ? 1 : count + 1);
        }

        List<LibraryItem> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            items.add(new LibraryItem(
                    category,
                    entry.getKey(),
                    getAggregateSubtitle(aggregateType),
                    getCountTrailing(entry.getValue()),
                    null));
        }
        return items;
    }

    private String getAggregateSubtitle(AggregateType aggregateType) {
        if (applicationContext == null) {
            return "";
        }
        if (aggregateType == AggregateType.ARTIST) {
            return applicationContext.getString(R.string.library_subtitle_artist_group);
        }
        if (aggregateType == AggregateType.ALBUM) {
            return applicationContext.getString(R.string.library_subtitle_album_group);
        }
        return applicationContext.getString(R.string.library_subtitle_folder_group);
    }

    private String getCountTrailing(int count) {
        if (applicationContext == null) {
            return String.valueOf(count);
        }
        return applicationContext.getString(R.string.library_count_suffix, count);
    }

    private boolean contains(String source, String query) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalizeQuery(String query) {
        return query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    }

    private String formatDuration(long durationMs) {
        long totalSeconds = durationMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void notifyListeners() {
        mainHandler.post(() -> {
            for (Listener listener : listeners) {
                listener.onMusicLibraryChanged();
            }
        });
    }

    private enum AggregateType {
        ARTIST,
        ALBUM,
        FOLDER
    }
}
