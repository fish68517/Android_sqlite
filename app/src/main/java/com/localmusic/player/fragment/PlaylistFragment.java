package com.localmusic.player.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.localmusic.player.R;
import com.localmusic.player.activity.MainActivity;
import com.localmusic.player.adapter.LibraryItemAdapter;
import com.localmusic.player.model.LibraryCategory;
import com.localmusic.player.model.LibraryItem;
import com.localmusic.player.model.Song;
import com.localmusic.player.player.MusicPlayerManager;
import com.localmusic.player.repository.MusicLibraryRepository;
import com.localmusic.player.repository.UserMusicStateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaylistFragment extends Fragment
        implements MusicLibraryRepository.Listener, UserMusicStateRepository.Listener {

    private MusicLibraryRepository libraryRepository;
    private UserMusicStateRepository stateRepository;
    private MusicPlayerManager playerManager;

    private LibraryItemAdapter favoriteAdapter;
    private LibraryItemAdapter recentAdapter;

    private TextView textFavoriteCount;
    private TextView textRecentCount;
    private TextView textFavoriteEmpty;
    private TextView textRecentEmpty;
    private RecyclerView recyclerFavorites;
    private RecyclerView recyclerRecent;

    public PlaylistFragment() {
        super(R.layout.fragment_playlist);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        libraryRepository = MusicLibraryRepository.getInstance();
        stateRepository = UserMusicStateRepository.getInstance(requireContext());
        playerManager = MusicPlayerManager.getInstance(requireContext());

        textFavoriteCount = view.findViewById(R.id.text_favorite_count);
        textRecentCount = view.findViewById(R.id.text_recent_count);
        textFavoriteEmpty = view.findViewById(R.id.text_favorite_empty);
        textRecentEmpty = view.findViewById(R.id.text_recent_empty);
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        recyclerRecent = view.findViewById(R.id.recycler_recent);

        favoriteAdapter = new LibraryItemAdapter(item -> playCollection(buildFavoriteSongs(), item));
        recentAdapter = new LibraryItemAdapter(item -> playCollection(buildRecentSongs(), item));

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerFavorites.setAdapter(favoriteAdapter);
        recyclerFavorites.setNestedScrollingEnabled(false);

        recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRecent.setAdapter(recentAdapter);
        recyclerRecent.setNestedScrollingEnabled(false);

        bindState();
    }

    @Override
    public void onStart() {
        super.onStart();
        libraryRepository.addListener(this);
        stateRepository.addListener(this);
        bindState();
    }

    @Override
    public void onStop() {
        super.onStop();
        libraryRepository.removeListener(this);
        stateRepository.removeListener(this);
    }

    @Override
    public void onMusicLibraryChanged() {
        if (isAdded()) {
            bindState();
        }
    }

    @Override
    public void onUserMusicStateChanged() {
        if (isAdded()) {
            bindState();
        }
    }

    private void bindState() {
        List<Song> favoriteSongs = buildFavoriteSongs();
        List<Song> recentSongs = buildRecentSongs();

        textFavoriteCount.setText(getString(R.string.playlist_count_template, favoriteSongs.size()));
        textRecentCount.setText(getString(R.string.playlist_count_template, recentSongs.size()));

        favoriteAdapter.submitList(buildSongItems(favoriteSongs));
        recentAdapter.submitList(buildSongItems(recentSongs));

        bindEmptyState(textFavoriteEmpty, recyclerFavorites, favoriteSongs, R.string.playlist_empty_favorites);
        bindEmptyState(textRecentEmpty, recyclerRecent, recentSongs, R.string.playlist_empty_recent);
    }

    private void bindEmptyState(TextView emptyView,
                                RecyclerView recyclerView,
                                List<Song> songs,
                                int emptyMessageResId) {
        boolean libraryEmpty = libraryRepository.getSongCount() == 0;
        boolean showEmpty = songs.isEmpty();

        recyclerView.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        if (showEmpty) {
            emptyView.setText(libraryEmpty ? R.string.playlist_empty_scan : emptyMessageResId);
        }
    }

    private List<Song> buildFavoriteSongs() {
        return libraryRepository.getSongsByIds(stateRepository.getFavoriteSongIds());
    }

    private List<Song> buildRecentSongs() {
        return libraryRepository.getSongsByIds(stateRepository.getRecentSongIds());
    }

    private List<LibraryItem> buildSongItems(List<Song> songs) {
        List<LibraryItem> items = new ArrayList<>();
        for (Song song : songs) {
            items.add(new LibraryItem(
                    LibraryCategory.SONGS,
                    song.getTitle(),
                    song.getArtist() + " / " + song.getAlbum(),
                    formatDuration(song.getDuration()),
                    song));
        }
        return items;
    }

    private void playCollection(List<Song> songs, LibraryItem selectedItem) {
        Song targetSong = selectedItem.getSong();
        if (targetSong == null || songs.isEmpty()) {
            return;
        }

        int selectedIndex = songs.indexOf(targetSong);
        if (selectedIndex < 0) {
            selectedIndex = 0;
        }
        playerManager.playQueue(songs, selectedIndex);
        ((MainActivity) requireActivity()).openPlayerPage();
    }

    private String formatDuration(long durationMs) {
        long totalSeconds = durationMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
