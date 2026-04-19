package com.localmusic.player.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.localmusic.player.R;
import com.localmusic.player.databinding.ActivityPlayerBinding;
import com.localmusic.player.model.LyricLine;
import com.localmusic.player.model.Song;
import com.localmusic.player.player.MusicPlayerManager;
import com.localmusic.player.repository.UserMusicStateRepository;
import com.localmusic.player.util.LocalLyricParser;
import com.localmusic.player.util.TimeFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity
        implements MusicPlayerManager.Listener, UserMusicStateRepository.Listener {

    private ActivityPlayerBinding binding;
    private MusicPlayerManager playerManager;
    private UserMusicStateRepository userMusicStateRepository;
    private boolean fromUserSeeking;
    private long lyricSongId = -1L;
    private final List<LyricLine> lyricLines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playerManager = MusicPlayerManager.getInstance(this);
        userMusicStateRepository = UserMusicStateRepository.getInstance(this);

        setSupportActionBar(binding.playerToolbar);
        binding.playerToolbar.setNavigationOnClickListener(v -> finish());

        binding.buttonPlayPause.setOnClickListener(v -> playerManager.togglePlayPause());
        binding.buttonPrevious.setOnClickListener(v -> playerManager.playPrevious());
        binding.buttonNext.setOnClickListener(v -> playerManager.playNext());
        binding.buttonFavorite.setOnClickListener(v -> toggleFavorite());
        binding.seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.textProgressStart.setText(TimeFormatUtils.formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                fromUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fromUserSeeking = false;
                playerManager.seekTo(seekBar.getProgress());
            }
        });

        bindPlayerState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerManager.addListener(this);
        userMusicStateRepository.addListener(this);
        bindPlayerState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerManager.removeListener(this);
        userMusicStateRepository.removeListener(this);
    }

    @Override
    public void onPlayerStateChanged() {
        bindPlayerState();
    }

    @Override
    public void onUserMusicStateChanged() {
        bindFavoriteState(playerManager.getCurrentSong());
    }

    private void bindPlayerState() {
        Song song = playerManager.getCurrentSong();
        if (song == null) {
            binding.textSongName.setText(R.string.player_empty_title);
            binding.textSongMeta.setText(R.string.player_empty_desc);
            binding.textPlayerStatus.setText(R.string.player_status_empty);
            binding.textQueueSummary.setText(R.string.player_queue_empty);
            binding.buttonPlayPause.setEnabled(false);
            binding.buttonPrevious.setEnabled(false);
            binding.buttonNext.setEnabled(false);
            binding.buttonFavorite.setEnabled(false);
            binding.seekProgress.setEnabled(false);
            binding.seekProgress.setProgress(0);
            binding.seekProgress.setMax(100);
            binding.textProgressStart.setText(R.string.player_progress_start);
            binding.textProgressEnd.setText(R.string.player_progress_start);
            binding.buttonPlayPause.setImageResource(android.R.drawable.ic_media_play);
            lyricSongId = -1L;
            lyricLines.clear();
            bindLyricState(0);
            return;
        }

        binding.textSongName.setText(song.getTitle());
        binding.textSongMeta.setText(getString(R.string.player_song_meta_live, song.getArtist(), song.getAlbum()));
        binding.textPlayerStatus.setText(playerManager.isPlaying()
                ? R.string.player_status_playing
                : R.string.player_status_paused);
        binding.textQueueSummary.setText(getString(
                R.string.player_queue_summary,
                playerManager.getCurrentIndex() + 1,
                playerManager.getQueueSize()));

        int duration = Math.max(playerManager.getDuration(), 1);
        int position = Math.min(playerManager.getCurrentPosition(), duration);
        binding.seekProgress.setEnabled(true);
        binding.seekProgress.setMax(duration);
        if (!fromUserSeeking) {
            binding.seekProgress.setProgress(position);
            binding.textProgressStart.setText(TimeFormatUtils.formatDuration(position));
        }
        binding.textProgressEnd.setText(TimeFormatUtils.formatDuration(duration));
        binding.buttonPlayPause.setEnabled(true);
        binding.buttonPrevious.setEnabled(playerManager.hasPrevious());
        binding.buttonNext.setEnabled(playerManager.hasNext());
        binding.buttonPlayPause.setImageResource(
                playerManager.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);

        bindFavoriteState(song);
        ensureLyricsLoaded(song);
        bindLyricState(position);
    }

    private void toggleFavorite() {
        Song currentSong = playerManager.getCurrentSong();
        if (currentSong == null) {
            return;
        }

        boolean wasFavorite = userMusicStateRepository.isFavorite(currentSong.getId());
        userMusicStateRepository.toggleFavorite(currentSong);
        Toast.makeText(
                this,
                wasFavorite ? R.string.player_favorite_removed : R.string.player_favorite_added,
                Toast.LENGTH_SHORT).show();
    }

    private void bindFavoriteState(Song song) {
        boolean enabled = song != null;
        binding.buttonFavorite.setEnabled(enabled);
        if (!enabled) {
            binding.buttonFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            binding.buttonFavorite.setContentDescription(getString(R.string.player_action_favorite));
            return;
        }

        boolean favorite = userMusicStateRepository.isFavorite(song.getId());
        binding.buttonFavorite.setImageResource(
                favorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        binding.buttonFavorite.setContentDescription(getString(
                favorite ? R.string.player_action_unfavorite : R.string.player_action_favorite));
    }

    private void ensureLyricsLoaded(Song song) {
        if (lyricSongId == song.getId()) {
            return;
        }
        lyricSongId = song.getId();
        lyricLines.clear();
        lyricLines.addAll(LocalLyricParser.parse(song));
    }

    private void bindLyricState(int positionMs) {
        if (lyricLines.isEmpty()) {
            binding.textLyricCurrent.setText(R.string.player_lyrics_empty);
            binding.textLyricCurrent.setTextColor(getColor(R.color.text_secondary));
            binding.textLyricNext.setText(R.string.player_lyrics_waiting);
            binding.textLyricStatus.setVisibility(View.GONE);
            return;
        }

        int currentIndex = findCurrentLyricIndex(positionMs);
        LyricLine currentLine = lyricLines.get(currentIndex);
        binding.textLyricCurrent.setText(currentLine.getText());
        binding.textLyricCurrent.setTextColor(getColor(R.color.text_primary));
        binding.textLyricStatus.setVisibility(View.VISIBLE);
        binding.textLyricStatus.setText(getString(
                R.string.player_lyric_time_point,
                TimeFormatUtils.formatDuration((int) currentLine.getTimestampMs())));

        if (currentIndex + 1 < lyricLines.size()) {
            binding.textLyricNext.setText(getString(
                    R.string.player_lyrics_next_preview,
                    lyricLines.get(currentIndex + 1).getText()));
        } else {
            binding.textLyricNext.setText(R.string.player_lyrics_none_next);
        }
    }

    private int findCurrentLyricIndex(int positionMs) {
        int currentIndex = 0;
        for (int i = 0; i < lyricLines.size(); i++) {
            if (lyricLines.get(i).getTimestampMs() <= positionMs) {
                currentIndex = i;
            } else {
                break;
            }
        }
        return currentIndex;
    }
}
