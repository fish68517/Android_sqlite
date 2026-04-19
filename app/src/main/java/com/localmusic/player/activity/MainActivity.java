package com.localmusic.player.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.localmusic.player.R;
import com.localmusic.player.databinding.ActivityMainBinding;
import com.localmusic.player.fragment.AiAssistantFragment;
import com.localmusic.player.fragment.HomeFragment;
import com.localmusic.player.fragment.LibraryFragment;
import com.localmusic.player.fragment.MineFragment;
import com.localmusic.player.fragment.PlaylistFragment;
import com.localmusic.player.model.Song;
import com.localmusic.player.player.MusicPlayerManager;
import com.localmusic.player.repository.MusicLibraryRepository;
import com.localmusic.player.util.TimeFormatUtils;

public class MainActivity extends AppCompatActivity
        implements MusicLibraryRepository.Listener, MusicPlayerManager.Listener {

    private static final int[] MENU_IDS = {
            R.id.menu_home,
            R.id.menu_library,
            R.id.menu_playlist,
            R.id.menu_ai,
            R.id.menu_mine
    };

    private ActivityMainBinding binding;
    private MusicLibraryRepository repository;
    private MusicPlayerManager playerManager;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = MusicLibraryRepository.getInstance();
        playerManager = MusicPlayerManager.getInstance(this);
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        repository.scan(this, true);
                    } else {
                        Toast.makeText(this, R.string.permission_request_denied, Toast.LENGTH_SHORT).show();
                        bindMiniPlayer();
                    }
                });

        setSupportActionBar(binding.topToolbar);
        setupMiniPlayer();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.menu_home);
        } else {
            updatePageDecor(binding.bottomNavigation.getSelectedItemId());
        }

        if (hasAudioPermission()) {
            repository.scan(this, false);
        } else {
            bindMiniPlayer();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        repository.addListener(this);
        playerManager.addListener(this);
        bindMiniPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        repository.removeListener(this);
        playerManager.removeListener(this);
    }

    @Override
    public void onMusicLibraryChanged() {
        bindMiniPlayer();
    }

    @Override
    public void onPlayerStateChanged() {
        bindMiniPlayer();
    }

    public void requestMusicScan(boolean force) {
        if (hasAudioPermission()) {
            repository.scan(this, force);
            return;
        }
        permissionLauncher.launch(getReadAudioPermission());
    }

    public boolean hasAudioPermission() {
        return ContextCompat.checkSelfPermission(this, getReadAudioPermission())
                == PackageManager.PERMISSION_GRANTED;
    }

    public void openPlayerPage() {
        startActivity(new Intent(this, PlayerActivity.class));
    }

    private void setupMiniPlayer() {
        binding.miniPlayerCard.setOnClickListener(v -> openPlayerPage());
        binding.buttonPlayStub.setOnClickListener(v -> {
            if (playerManager.hasCurrentSong()) {
                playerManager.togglePlayPause();
            } else {
                Toast.makeText(this, R.string.message_player_queue_empty, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switchFragment(item.getItemId());
            return true;
        });
    }

    private void switchFragment(int menuId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String targetTag = getFragmentTag(menuId);
        Fragment targetFragment = fragmentManager.findFragmentByTag(targetTag);

        if (targetFragment != null && targetFragment.isVisible()) {
            updatePageDecor(menuId);
            return;
        }

        transaction.setReorderingAllowed(true);

        for (int id : MENU_IDS) {
            Fragment existingFragment = fragmentManager.findFragmentByTag(getFragmentTag(id));
            if (existingFragment != null) {
                transaction.hide(existingFragment);
            }
        }

        if (targetFragment == null) {
            targetFragment = createFragment(menuId);
            transaction.add(R.id.fragment_container, targetFragment, targetTag);
        } else {
            transaction.show(targetFragment);
        }

        transaction.commit();
        updatePageDecor(menuId);
    }

    private Fragment createFragment(int menuId) {
        if (menuId == R.id.menu_library) {
            return new LibraryFragment();
        }
        if (menuId == R.id.menu_playlist) {
            return new PlaylistFragment();
        }
        if (menuId == R.id.menu_ai) {
            return new AiAssistantFragment();
        }
        if (menuId == R.id.menu_mine) {
            return new MineFragment();
        }
        return new HomeFragment();
    }

    @NonNull
    private String getFragmentTag(int menuId) {
        if (menuId == R.id.menu_library) {
            return "library";
        }
        if (menuId == R.id.menu_playlist) {
            return "playlist";
        }
        if (menuId == R.id.menu_ai) {
            return "ai";
        }
        if (menuId == R.id.menu_mine) {
            return "mine";
        }
        return "home";
    }

    private void updateToolbarTitle(int menuId) {
        int titleResId;
        if (menuId == R.id.menu_library) {
            titleResId = R.string.title_library;
        } else if (menuId == R.id.menu_playlist) {
            titleResId = R.string.title_playlist;
        } else if (menuId == R.id.menu_ai) {
            titleResId = R.string.title_ai;
        } else if (menuId == R.id.menu_mine) {
            titleResId = R.string.title_mine;
        } else {
            titleResId = R.string.title_home;
        }
        binding.topToolbar.setTitle(titleResId);
    }

    private void updatePageDecor(int menuId) {
        updateToolbarTitle(menuId);
        binding.miniPlayerCard.setVisibility(menuId == R.id.menu_home ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void bindMiniPlayer() {
        Song currentSong = playerManager.getCurrentSong();
        if (currentSong != null) {
            binding.textMiniPlayerTitle.setText(currentSong.getTitle());
            binding.textMiniPlayerSubtitle.setText(getString(
                    R.string.mini_player_now_playing,
                    currentSong.getArtist(),
                    TimeFormatUtils.formatDuration(playerManager.getCurrentPosition()),
                    TimeFormatUtils.formatDuration(playerManager.getDuration())));
            binding.buttonPlayStub.setImageResource(
                    playerManager.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
            return;
        }

        binding.buttonPlayStub.setImageResource(android.R.drawable.ic_media_play);

        if (repository.isLoading()) {
            binding.textMiniPlayerTitle.setText(R.string.mini_player_scanning_title);
            binding.textMiniPlayerSubtitle.setText(R.string.mini_player_scanning_subtitle);
        } else if (repository.getSongCount() > 0) {
            binding.textMiniPlayerTitle.setText(getString(R.string.mini_player_ready_title, repository.getSongCount()));
            binding.textMiniPlayerSubtitle.setText(getString(
                    R.string.mini_player_ready_subtitle,
                    repository.getArtistCount(),
                    repository.getAlbumCount(),
                    repository.getFolderCount()));
        } else if (!hasAudioPermission()) {
            binding.textMiniPlayerTitle.setText(R.string.mini_player_permission_title);
            binding.textMiniPlayerSubtitle.setText(R.string.mini_player_permission_subtitle);
        } else {
            binding.textMiniPlayerTitle.setText(R.string.mini_player_empty_title);
            binding.textMiniPlayerSubtitle.setText(R.string.mini_player_empty_subtitle);
        }
    }

    private String getReadAudioPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;
    }
}
