package com.archive.app.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.archive.app.fragment.HomeFragment;
import com.archive.app.fragment.ItineraryFragment;
import com.archive.app.fragment.PostFragment;
import com.archive.app.fragment.ProfileFragment;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    private final FragmentManager fm = getSupportFragmentManager();
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment postFragment = new PostFragment();
    private final Fragment itineraryFragment = new ItineraryFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private Fragment active = homeFragment;

    private MediaPlayer mediaPlayer;
    private SeekBar musicSeekBar;
    private final Handler musicHandler = new Handler();

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        int id = item.getItemId();
        if (id == R.id.navigation_home) {
            switchFragment(homeFragment);
            return true;
        } else if (id == R.id.navigation_post) {
            switchFragment(postFragment);
            return true;
        } else if (id == R.id.navigation_itinerary) {
            switchFragment(itineraryFragment);
            return true;
        } else if (id == R.id.navigation_profile) {
            switchFragment(profileFragment);
            return true;
        }
        return false;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.fragment_container, profileFragment, "4").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, itineraryFragment, "3").hide(itineraryFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, postFragment, "2").hide(postFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();

         setupMusicPlayer();
    }

    private void setupMusicPlayer() {
        musicSeekBar = findViewById(R.id.music_seek_bar);
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.music);
            if (mediaPlayer == null) {
                Log.e("MainActivity", "MediaPlayer creation failed. Is res/raw/music.mp3 missing?");
                return;
            }
            mediaPlayer.setLooping(true);
            musicSeekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.start();
            updateSeekBar();

            musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } catch (Exception e) {
            Log.e("MainActivity", "Error setting up MediaPlayer", e);
            Toast.makeText(this, "无法加载音乐文件", Toast.LENGTH_SHORT).show();
        }
    }

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                musicHandler.postDelayed(this, 1000);
            }
        }
    };

    private void updateSeekBar() {
        musicHandler.post(updater);
    }

    private void switchFragment(Fragment nextFragment) {
        if (active != nextFragment) {
            fm.beginTransaction().hide(active).show(nextFragment).commit();
            active = nextFragment;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateSeekBar();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        musicHandler.removeCallbacks(updater);
    }
}