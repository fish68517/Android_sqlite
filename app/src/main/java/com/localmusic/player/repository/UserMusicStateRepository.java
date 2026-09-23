package com.localmusic.player.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.localmusic.player.model.Song;
import com.localmusic.player.model.UserPlaylist;
import com.localmusic.player.player.PlaybackMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class UserMusicStateRepository {

    public interface Listener {
        void onUserMusicStateChanged();
    }

    private static final String PREFS_NAME = "music_user_state";
    private static final String KEY_FAVORITE_IDS = "favorite_ids";
    private static final String KEY_RECENT_IDS = "recent_ids";
    private static final String KEY_PLAYBACK_MODE = "playback_mode";
    private static final String KEY_USER_PLAYLISTS = "user_playlists";
    private static final int MAX_RECENT_SIZE = 30;

    private static volatile UserMusicStateRepository instance;

    private final SharedPreferences sharedPreferences;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Gson gson = new Gson();
    private final Set<Listener> listeners = new CopyOnWriteArraySet<>();
    private final LinkedHashSet<Long> favoriteSongIds = new LinkedHashSet<>();
    private final List<Long> recentSongIds = new ArrayList<>();
    private final List<UserPlaylist> userPlaylists = new ArrayList<>();
    private PlaybackMode playbackMode = PlaybackMode.SEQUENCE;

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

    public synchronized PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    public void setPlaybackMode(@NonNull PlaybackMode mode) {
        synchronized (this) {
            playbackMode = mode;
            persistLocked();
        }
        notifyListeners();
    }

    public synchronized List<UserPlaylist> getUserPlaylists() {
        return new ArrayList<>(userPlaylists);
    }

    public synchronized UserPlaylist getUserPlaylist(String playlistId) {
        return findPlaylistLocked(playlistId);
    }

    public UserPlaylist createPlaylist(@NonNull String name) {
        UserPlaylist playlist;
        synchronized (this) {
            long now = System.currentTimeMillis();
            playlist = new UserPlaylist(UUID.randomUUID().toString(), name.trim(), now, now);
            userPlaylists.add(0, playlist);
            persistLocked();
        }
        notifyListeners();
        return playlist;
    }

    public void renamePlaylist(@NonNull String playlistId, @NonNull String name) {
        synchronized (this) {
            UserPlaylist playlist = findPlaylistLocked(playlistId);
            if (playlist == null) {
                return;
            }
            playlist.setName(name.trim());
            playlist.setUpdatedAt(System.currentTimeMillis());
            persistLocked();
        }
        notifyListeners();
    }

    public void deletePlaylist(@NonNull String playlistId) {
        synchronized (this) {
            for (int i = 0; i < userPlaylists.size(); i++) {
                if (playlistId.equals(userPlaylists.get(i).getId())) {
                    userPlaylists.remove(i);
                    persistLocked();
                    notifyListeners();
                    return;
                }
            }
        }
    }

    public boolean addSongToPlaylist(@NonNull String playlistId, @NonNull Song song) {
        synchronized (this) {
            UserPlaylist playlist = findPlaylistLocked(playlistId);
            if (playlist == null) {
                return false;
            }
            List<Long> songIds = playlist.getSongIds();
            if (songIds.contains(song.getId())) {
                return false;
            }
            songIds.add(song.getId());
            playlist.setUpdatedAt(System.currentTimeMillis());
            persistLocked();
        }
        notifyListeners();
        return true;
    }

    public void removeSongFromPlaylist(@NonNull String playlistId, long songId) {
        synchronized (this) {
            UserPlaylist playlist = findPlaylistLocked(playlistId);
            if (playlist == null) {
                return;
            }
            playlist.getSongIds().remove(songId);
            playlist.setUpdatedAt(System.currentTimeMillis());
            persistLocked();
        }
        notifyListeners();
    }

    private void loadState() {
        synchronized (this) {
            favoriteSongIds.clear();
            favoriteSongIds.addAll(parseIds(sharedPreferences.getString(KEY_FAVORITE_IDS, "")));

            recentSongIds.clear();
            recentSongIds.addAll(parseIds(sharedPreferences.getString(KEY_RECENT_IDS, "")));

            playbackMode = parsePlaybackMode(sharedPreferences.getString(KEY_PLAYBACK_MODE, ""));

            userPlaylists.clear();
            userPlaylists.addAll(parsePlaylists(sharedPreferences.getString(KEY_USER_PLAYLISTS, "")));
        }
    }

    private void persistLocked() {
        sharedPreferences.edit()
                .putString(KEY_FAVORITE_IDS, joinIds(favoriteSongIds))
                .putString(KEY_RECENT_IDS, joinIds(recentSongIds))
                .putString(KEY_PLAYBACK_MODE, playbackMode.name())
                .putString(KEY_USER_PLAYLISTS, gson.toJson(userPlaylists))
                .apply();
    }

    private PlaybackMode parsePlaybackMode(String source) {
        if (source == null || source.trim().isEmpty()) {
            return PlaybackMode.SEQUENCE;
        }
        try {
            return PlaybackMode.valueOf(source.trim());
        } catch (IllegalArgumentException ignored) {
            return PlaybackMode.SEQUENCE;
        }
    }

    private List<UserPlaylist> parsePlaylists(String source) {
        if (source == null || source.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<UserPlaylist>>() {
            }.getType();
            List<UserPlaylist> playlists = gson.fromJson(source, type);
            return playlists == null ? new ArrayList<>() : playlists;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
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

    private UserPlaylist findPlaylistLocked(String playlistId) {
        if (playlistId == null) {
            return null;
        }
        for (UserPlaylist playlist : userPlaylists) {
            if (playlistId.equals(playlist.getId())) {
                return playlist;
            }
        }
        return null;
    }

    private void notifyListeners() {
        mainHandler.post(() -> {
            for (Listener listener : listeners) {
                listener.onUserMusicStateChanged();
            }
        });
    }
}
