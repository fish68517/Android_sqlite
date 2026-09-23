package com.localmusic.player.player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.localmusic.player.model.Song;
import com.localmusic.player.repository.UserMusicStateRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

public class MusicPlayerManager {

    public interface Listener {
        void onPlayerStateChanged();
    }

    private static final long PROGRESS_INTERVAL_MS = 500L;
    private static volatile MusicPlayerManager instance;

    private final Context appContext;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final CopyOnWriteArraySet<Listener> listeners = new CopyOnWriteArraySet<>();
    private final List<Song> queue = new ArrayList<>();
    private final Random random = new Random();
    private final UserMusicStateRepository stateRepository;
    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            notifyListeners();
            if (mediaPlayer != null && (prepared || mediaPlayer.isPlaying())) {
                mainHandler.postDelayed(this, PROGRESS_INTERVAL_MS);
            }
        }
    };

    private MediaPlayer mediaPlayer;
    private CountDownTimer sleepTimer;
    private int currentIndex = -1;
    private PlaybackMode playbackMode = PlaybackMode.SEQUENCE;
    private boolean prepared;
    private boolean playWhenPrepared;
    private boolean stopAfterCurrentSong;
    private long sleepTimerEndAtMs;

    private MusicPlayerManager(Context context) {
        appContext = context.getApplicationContext();
        stateRepository = UserMusicStateRepository.getInstance(appContext);
        playbackMode = stateRepository.getPlaybackMode();
    }

    public static MusicPlayerManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicPlayerManager.class) {
                if (instance == null) {
                    instance = new MusicPlayerManager(context);
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

    public void playQueue(@NonNull List<Song> songs, int startIndex) {
        if (songs.isEmpty() || startIndex < 0 || startIndex >= songs.size()) {
            return;
        }
        queue.clear();
        queue.addAll(songs);
        currentIndex = startIndex;
        playCurrentSong(true);
    }

    public void togglePlayPause() {
        if (!hasCurrentSong()) {
            return;
        }
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public void play() {
        if (!hasCurrentSong()) {
            return;
        }
        if (prepared && mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startProgressUpdates();
            notifyListeners();
            return;
        }
        playCurrentSong(true);
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopProgressUpdates();
            notifyListeners();
        }
    }

    public void seekTo(int positionMs) {
        if (prepared && mediaPlayer != null) {
            mediaPlayer.seekTo(positionMs);
            notifyListeners();
        }
    }

    public void playNext() {
        int nextIndex = resolveNextIndexByUserAction();
        if (nextIndex >= 0) {
            currentIndex = nextIndex;
            playCurrentSong(true);
        }
    }

    public void playPrevious() {
        if (!hasCurrentSong()) {
            return;
        }
        if (getCurrentPosition() > 3000) {
            seekTo(0);
            return;
        }
        if (currentIndex > 0) {
            currentIndex--;
            playCurrentSong(true);
        } else if (playbackMode == PlaybackMode.LIST_LOOP && queue.size() > 1) {
            currentIndex = queue.size() - 1;
            playCurrentSong(true);
        } else {
            seekTo(0);
        }
    }

    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    public void switchPlaybackMode() {
        setPlaybackMode(playbackMode.next());
    }

    public void setPlaybackMode(@NonNull PlaybackMode mode) {
        playbackMode = mode;
        stateRepository.setPlaybackMode(mode);
        notifyListeners();
    }

    public void startSleepTimer(long durationMs) {
        cancelSleepTimerInternal(false);
        stopAfterCurrentSong = false;
        sleepTimerEndAtMs = System.currentTimeMillis() + durationMs;
        sleepTimer = new CountDownTimer(durationMs, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                notifyListeners();
            }

            @Override
            public void onFinish() {
                sleepTimer = null;
                sleepTimerEndAtMs = 0L;
                pause();
                notifyListeners();
            }
        };
        sleepTimer.start();
        notifyListeners();
    }

    public void stopAfterCurrentSong() {
        cancelSleepTimerInternal(false);
        stopAfterCurrentSong = true;
        notifyListeners();
    }

    public void cancelSleepTimer() {
        cancelSleepTimerInternal(true);
    }

    public boolean hasSleepTimer() {
        return sleepTimer != null || stopAfterCurrentSong;
    }

    public boolean isStopAfterCurrentSong() {
        return stopAfterCurrentSong;
    }

    public long getSleepRemainingMs() {
        if (sleepTimer == null || sleepTimerEndAtMs <= 0L) {
            return 0L;
        }
        return Math.max(sleepTimerEndAtMs - System.currentTimeMillis(), 0L);
    }

    public boolean isPlaying() {
        return prepared && mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isPrepared() {
        return prepared;
    }

    public Song getCurrentSong() {
        if (!hasCurrentSong()) {
            return null;
        }
        return queue.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getCurrentPosition() {
        if (!prepared || mediaPlayer == null) {
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        if (!prepared || mediaPlayer == null) {
            Song currentSong = getCurrentSong();
            return currentSong == null ? 0 : (int) currentSong.getDuration();
        }
        return mediaPlayer.getDuration();
    }

    public boolean hasCurrentSong() {
        return currentIndex >= 0 && currentIndex < queue.size();
    }

    public boolean hasNext() {
        if (!hasCurrentSong()) {
            return false;
        }
        if (queue.size() > 1 && (playbackMode == PlaybackMode.LIST_LOOP || playbackMode == PlaybackMode.SHUFFLE)) {
            return true;
        }
        return currentIndex < queue.size() - 1;
    }

    public boolean hasPrevious() {
        return currentIndex > 0 || getCurrentPosition() > 0;
    }

    private void playCurrentSong(boolean autoStart) {
        Song currentSong = getCurrentSong();
        if (currentSong == null) {
            return;
        }

        releaseInternal();

        prepared = false;
        playWhenPrepared = autoStart;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        mediaPlayer.setOnPreparedListener(mp -> {
            prepared = true;
            UserMusicStateRepository.getInstance(appContext).recordRecentSong(currentSong);
            if (playWhenPrepared) {
                mp.start();
            }
            startProgressUpdates();
            notifyListeners();
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            if (stopAfterCurrentSong) {
                stopAfterCurrentSong = false;
                mp.seekTo(0);
                mp.pause();
                stopProgressUpdates();
                notifyListeners();
                return;
            }

            int nextIndex = resolveNextIndexOnCompletion();
            if (nextIndex >= 0) {
                currentIndex = nextIndex;
                playCurrentSong(true);
            } else {
                mp.seekTo(0);
                mp.pause();
                stopProgressUpdates();
                notifyListeners();
            }
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            stopProgressUpdates();
            notifyListeners();
            return false;
        });

        try {
            mediaPlayer.setDataSource(appContext, currentSong.getContentUri());
            mediaPlayer.prepareAsync();
            notifyListeners();
        } catch (IOException | IllegalArgumentException e) {
            releaseInternal();
            notifyListeners();
        }
    }

    private void releaseInternal() {
        stopProgressUpdates();
        prepared = false;
        playWhenPrepared = false;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
            } catch (IllegalStateException ignored) {
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startProgressUpdates() {
        stopProgressUpdates();
        mainHandler.post(progressRunnable);
    }

    private void stopProgressUpdates() {
        mainHandler.removeCallbacks(progressRunnable);
    }

    private void notifyListeners() {
        mainHandler.post(() -> {
            for (Listener listener : listeners) {
                listener.onPlayerStateChanged();
            }
        });
    }

    private int resolveNextIndexOnCompletion() {
        if (!hasCurrentSong()) {
            return -1;
        }
        if (playbackMode == PlaybackMode.SINGLE_LOOP) {
            return currentIndex;
        }
        return resolveNextIndexByUserAction();
    }

    private int resolveNextIndexByUserAction() {
        if (!hasCurrentSong()) {
            return -1;
        }
        if (queue.size() == 1) {
            return playbackMode == PlaybackMode.SEQUENCE ? -1 : 0;
        }
        if (playbackMode == PlaybackMode.SHUFFLE) {
            int nextIndex = currentIndex;
            while (nextIndex == currentIndex) {
                nextIndex = random.nextInt(queue.size());
            }
            return nextIndex;
        }
        if (currentIndex < queue.size() - 1) {
            return currentIndex + 1;
        }
        return playbackMode == PlaybackMode.LIST_LOOP ? 0 : -1;
    }

    private void cancelSleepTimerInternal(boolean notify) {
        if (sleepTimer != null) {
            sleepTimer.cancel();
            sleepTimer = null;
        }
        sleepTimerEndAtMs = 0L;
        stopAfterCurrentSong = false;
        if (notify) {
            notifyListeners();
        }
    }
}
