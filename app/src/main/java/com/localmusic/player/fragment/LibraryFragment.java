package com.localmusic.player.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.localmusic.player.R;
import com.localmusic.player.activity.MainActivity;
import com.localmusic.player.adapter.LibraryItemAdapter;
import com.localmusic.player.model.LibraryCategory;
import com.localmusic.player.model.LibraryItem;
import com.localmusic.player.model.Song;
import com.localmusic.player.player.MusicPlayerManager;
import com.localmusic.player.repository.MusicLibraryRepository;

import java.util.List;

public class LibraryFragment extends Fragment implements MusicLibraryRepository.Listener {

    private TextInputEditText inputSearch;
    private TabLayout tabLayout;
    private TextView textSongCount;
    private TextView textArtistCount;
    private TextView textAlbumCount;
    private TextView textFolderCount;
    private TextView textEmptyTitle;
    private TextView textEmptyDesc;
    private ProgressBar progressLoading;
    private MaterialButton buttonScanNow;
    private MaterialButton buttonEmptyAction;
    private View layoutEmpty;
    private RecyclerView recyclerLibrary;

    private LibraryCategory currentCategory = LibraryCategory.SONGS;
    private MusicLibraryRepository repository;
    private MusicPlayerManager playerManager;
    private LibraryItemAdapter adapter;

    public LibraryFragment() {
        super(R.layout.fragment_library);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = MusicLibraryRepository.getInstance();
        playerManager = MusicPlayerManager.getInstance(requireContext());

        inputSearch = view.findViewById(R.id.input_search);
        tabLayout = view.findViewById(R.id.tab_library);
        textSongCount = view.findViewById(R.id.text_count_song);
        textArtistCount = view.findViewById(R.id.text_count_artist);
        textAlbumCount = view.findViewById(R.id.text_count_album);
        textFolderCount = view.findViewById(R.id.text_count_folder);
        textEmptyTitle = view.findViewById(R.id.text_empty_title);
        textEmptyDesc = view.findViewById(R.id.text_empty_desc);
        progressLoading = view.findViewById(R.id.progress_loading);
        buttonScanNow = view.findViewById(R.id.button_scan_now);
        buttonEmptyAction = view.findViewById(R.id.button_empty_action);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        recyclerLibrary = view.findViewById(R.id.recycler_library);

        adapter = new LibraryItemAdapter(item -> {
            if (item.getCategory() != LibraryCategory.SONGS || item.getSong() == null) {
                Toast.makeText(requireContext(), R.string.library_group_click_tip, Toast.LENGTH_SHORT).show();
                return;
            }

            String query = inputSearch.getText() == null ? "" : inputSearch.getText().toString().trim();
            List<Song> songs = repository.getSongs(query);
            int selectedIndex = songs.indexOf(item.getSong());
            if (selectedIndex < 0) {
                selectedIndex = 0;
            }
            playerManager.playQueue(songs, selectedIndex);
            ((MainActivity) requireActivity()).openPlayerPage();
        });

        recyclerLibrary.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerLibrary.setAdapter(adapter);

        setupTabs();
        setupSearch();

        buttonScanNow.setOnClickListener(v -> ((MainActivity) requireActivity()).requestMusicScan(true));
        buttonEmptyAction.setOnClickListener(v -> ((MainActivity) requireActivity()).requestMusicScan(true));

        bindState();
    }

    @Override
    public void onStart() {
        super.onStart();
        repository.addListener(this);
        bindState();
    }

    @Override
    public void onStop() {
        super.onStop();
        repository.removeListener(this);
    }

    @Override
    public void onMusicLibraryChanged() {
        if (isAdded()) {
            bindState();
        }
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.library_tab_song));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.library_tab_artist));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.library_tab_album));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.library_tab_folder));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 1) {
                    currentCategory = LibraryCategory.ARTISTS;
                } else if (position == 2) {
                    currentCategory = LibraryCategory.ALBUMS;
                } else if (position == 3) {
                    currentCategory = LibraryCategory.FOLDERS;
                } else {
                    currentCategory = LibraryCategory.SONGS;
                }
                bindList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupSearch() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bindList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void bindState() {
        textSongCount.setText(getString(R.string.library_count_song, repository.getSongCount()));
        textArtistCount.setText(getString(R.string.library_count_artist, repository.getArtistCount()));
        textAlbumCount.setText(getString(R.string.library_count_album, repository.getAlbumCount()));
        textFolderCount.setText(getString(R.string.library_count_folder, repository.getFolderCount()));

        buttonScanNow.setEnabled(!repository.isLoading());
        progressLoading.setVisibility(repository.isLoading() ? View.VISIBLE : View.GONE);

        bindList();
    }

    private void bindList() {
        List<LibraryItem> items = repository.getLibraryItems(
                currentCategory,
                inputSearch.getText() == null ? "" : inputSearch.getText().toString().trim());
        adapter.submitList(items);

        boolean showEmpty = items.isEmpty() && !repository.isLoading();
        recyclerLibrary.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(showEmpty ? View.VISIBLE : View.GONE);

        if (repository.getSongCount() == 0) {
            textEmptyTitle.setText(R.string.library_empty_no_data_title);
            textEmptyDesc.setText(R.string.library_empty_no_data_desc);
            buttonEmptyAction.setText(R.string.action_scan_now);
        } else {
            textEmptyTitle.setText(R.string.library_empty_search_title);
            textEmptyDesc.setText(R.string.library_empty_search_desc);
            buttonEmptyAction.setText(R.string.action_scan_again);
        }
    }
}
