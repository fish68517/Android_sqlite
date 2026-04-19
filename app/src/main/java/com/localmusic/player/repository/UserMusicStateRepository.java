package com.localmusic.player.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.localmusic.player.model.Song;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class UserMusicStateRepository {

    public interface Listener {
        void onUserMusicStateChanged();
    }

    private static final String PREFS_NAME = "music_user_state";
    private static final String KEY_FAVORITE_IDS = "favorite_ids";
    private static final String KEY_RECENT_IDS = "recent_ids";
    private static final int MAX_RECENT_SIZE = 30;

    private static volatile UserMusicStateRepository instance;

    private final SharedPreferences sharedPreferences;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Set<Listener> listeners = new CopyOnWriteArraySet<>();
    private final LinkedHashSet<Long> favoriteSongIds = new LinkedHashSet<>();
    private final List<Long> recentSongIds = new ArrayList<>();

    private UserMusicStateRepository(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadState();
    }

    public static UserMusicStateRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (UserMusicStateRepository.class) {
                if (instance == null) {
                    instance = new UserMusicStateRepository(context);
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

    public synchronized boolean isFavorite(long songId) {
        return favoriteSongIds.contains(songId);
    }

    public void toggleFavorite(@NonNull Song song) {
        synchronized (this) {
            if (favoriteSongIds.contains(song.getId())) {
                favoriteSongIds.remove(song.getId());
            } else {
                favoriteSongIds.add(song.getId());
            }
            persistLocked();
        }
        notifyListeners();
    }

    public void recordRecentSong(@NonNull Song song) {
        synchronized (this) {
            Long songId = song.getId();
            recentSongIds.remove(songId);
            recentSongIds.add(0, songId);
            while (recentSongIds.size() > MAX_RECENT_SIZE) {
                recentSongIds.remove(recentSongIds.size() - 1);
            }
            persistLocked();
        }
        notifyListeners();
    }

    public synchronized List<Long> getFavoriteSongIds() {
        return new ArrayList<>(favoriteSongIds);
    }

    public synchronized List<Long> getRecentSongIds() {
        return new ArrayList<>(recentSongIds);
    }

    public synchronized int getFavoriteCount() {
        return favoriteSongIds.size();
    }

    public synchronized int getRecentCount() {
        return recentSongIds.size();
    }

    private void loadState() {
        synchronized (this) {
            favoriteSongIds.clear();
            favoriteSongIds.addAll(parseIds(sharedPreferences.getString(KEY_FAVORITE_IDS, "")));

            recentSongIds.clear();
            recentSongIds.addAll(parseIds(sharedPreferences.getString(KEY_RECENT_IDS, "")));
        }
    }

    private void persistLocked() {
        sharedPreferences.edit()
                .putString(KEY_FAVORITE_IDS, joinIds(favoriteSongIds))
                .putString(KEY_RECENT_IDS, joinIds(recentSongIds))
                .apply();
    }

    private List<Long> parseIds(String source) {
        List<Long> ids = new ArrayList<>();
        if (source == null || source.trim().isEmpty()) {
            return ids;
        }

        String[] tokens = source.split(",");
        for (String token : tokens) {
            String value = token == null ? "" : token.trim();
            if (value.isEmpty()) {
                continue;
            }
            try {
                ids.add(Long.parseLong(value));
            } catch (NumberFormatException ignored) {
            }
        }
        return ids;
    }

    private String joinIds(Iterable<Long> ids) {
        StringBuilder builder = new StringBuilder();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(id);
        }
        return builder.toString();
    }

    private void notifyListeners() {
        mainHandler.post(() -> {
            for (Listener listener : listeners) {
                listener.onUserMusicStateChanged();
            }
        });
    }
}
