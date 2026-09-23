package com.localmusic.player.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.localmusic.player.R;
import com.localmusic.player.adapter.PlaylistSongAdapter;
import com.localmusic.player.databinding.ActivityUserPlaylistDetailBinding;
import com.localmusic.player.model.Song;
import com.localmusic.player.model.UserPlaylist;
import com.localmusic.player.player.MusicPlayerManager;
import com.localmusic.player.repository.MusicLibraryRepository;
import com.localmusic.player.repository.UserMusicStateRepository;

import java.util.List;

public class UserPlaylistDetailActivity extends AppCompatActivity
        implements UserMusicStateRepository.Listener, MusicLibraryRepository.Listener {

    public static final String EXTRA_PLAYLIST_ID = "playlist_id";

    private ActivityUserPlaylistDetailBinding binding;
    private MusicLibraryRepository libraryRepository;
    private UserMusicStateRepository stateRepository;
    private MusicPlayerManager playerManager;
    private PlaylistSongAdapter adapter;
    private String playlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPlaylistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playlistId = getIntent().getStringExtra(EXTRA_PLAYLIST_ID);
        libraryRepository = MusicLibraryRepository.getInstance();
        stateRepository = UserMusicStateRepository.getInstance(this);
        playerManager = MusicPlayerManager.getInstance(this);

        setSupportActionBar(binding.playlistToolbar);
        binding.playlistToolbar.setNavigationOnClickListener(v -> finish());

        adapter = new PlaylistSongAdapter(new PlaylistSongAdapter.Listener() {
            @Override
            public void onSongClick(Song song) {
                playSong(song);
            }

            @Override
            public void onRemoveClick(Song song) {
                stateRepository.removeSongFromPlaylist(playlistId, song.getId());
                Toast.makeText(UserPlaylistDetailActivity.this, R.string.playlist_song_removed, Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerPlaylistSongs.setAdapter(adapter);

        binding.buttonAddSong.setOnClickListener(v -> showAddSongDialog());
        binding.buttonRename.setOnClickListener(v -> showRenameDialog());
        binding.buttonDelete.setOnClickListener(v -> showDeleteConfirmDialog());

        bindState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stateRepository.addListener(this);
        libraryRepository.addListener(this);
        bindState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stateRepository.removeListener(this);
        libraryRepository.removeListener(this);
    }

    @Override
    public void onUserMusicStateChanged() {
        bindState();
    }

    @Override
    public void onMusicLibraryChanged() {
        bindState();
    }

    private void bindState() {
        UserPlaylist playlist = stateRepository.getUserPlaylist(playlistId);
        if (playlist == null) {
            finish();
            return;
        }

        List<Song> songs = libraryRepository.getSongsByIds(playlist.getSongIds());
        binding.textPlaylistName.setText(playlist.getName());
        binding.textPlaylistCount.setText(getString(R.string.playlist_song_count, songs.size()));
        adapter.submitList(songs);

        boolean empty = songs.isEmpty();
        binding.recyclerPlaylistSongs.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void showAddSongDialog() {
        List<Song> songs = libraryRepository.getSongs("");
        if (songs.isEmpty()) {
            Toast.makeText(this, R.string.playlist_add_empty_library, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] titles = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            titles[i] = song.getTitle() + " - " + song.getArtist();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.playlist_add_song)
                .setItems(titles, (dialog, which) -> {
                    boolean added = stateRepository.addSongToPlaylist(playlistId, songs.get(which));
                    Toast.makeText(
                            this,
                            added ? R.string.playlist_song_added : R.string.playlist_song_exists,
                            Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showRenameDialog() {
        UserPlaylist playlist = stateRepository.getUserPlaylist(playlistId);
        if (playlist == null) {
            return;
        }

        EditText input = buildPlaylistNameInput();
        input.setText(playlist.getName());
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(this)
                .setTitle(R.string.playlist_rename_title)
                .setView(input)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    String name = input.getText() == null ? "" : input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        stateRepository.renamePlaylist(playlistId, name);
                    }
                })
                .show();
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.playlist_delete)
                .setMessage(R.string.playlist_delete_confirm)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    stateRepository.deletePlaylist(playlistId);
                    Toast.makeText(this, R.string.playlist_delete_done, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private EditText buildPlaylistNameInput() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.playlist_name_hint);
        int padding = getResources().getDimensionPixelSize(android.R.dimen.app_icon_size) / 4;
        input.setPadding(padding, padding, padding, padding);
        return input;
    }

    private void playSong(Song song) {
        UserPlaylist playlist = stateRepository.getUserPlaylist(playlistId);
        if (playlist == null) {
            return;
        }
        List<Song> songs = libraryRepository.getSongsByIds(playlist.getSongIds());
        int index = songs.indexOf(song);
        if (index < 0) {
            index = 0;
        }
        playerManager.playQueue(songs, index);
        startActivity(new Intent(this, PlayerActivity.class));
    }
}
