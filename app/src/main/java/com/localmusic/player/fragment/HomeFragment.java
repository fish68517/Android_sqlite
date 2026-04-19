package com.localmusic.player.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.localmusic.player.R;
import com.localmusic.player.activity.MainActivity;
import com.localmusic.player.activity.PlayerActivity;
import com.localmusic.player.repository.MusicLibraryRepository;

public class HomeFragment extends Fragment implements MusicLibraryRepository.Listener {

    private TextView textLibrarySummary;
    private TextView textPermissionSummary;
    private TextView textNextStepDesc;
    private MusicLibraryRepository repository;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = MusicLibraryRepository.getInstance();

        textLibrarySummary = view.findViewById(R.id.text_library_summary);
        textPermissionSummary = view.findViewById(R.id.text_permission_summary);
        textNextStepDesc = view.findViewById(R.id.text_next_step_desc);

        view.findViewById(R.id.button_scan_music).setOnClickListener(v ->
                ((MainActivity) requireActivity()).requestMusicScan(true));

        view.findViewById(R.id.button_open_player).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PlayerActivity.class)));

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

    private void bindState() {
        MainActivity activity = (MainActivity) requireActivity();

        if (repository.isLoading()) {
            textLibrarySummary.setText(R.string.home_library_loading);
        } else if (repository.getSongCount() > 0) {
            textLibrarySummary.setText(getString(
                    R.string.home_library_summary,
                    repository.getSongCount(),
                    repository.getArtistCount(),
                    repository.getAlbumCount(),
                    repository.getFolderCount()));
        } else {
            textLibrarySummary.setText(R.string.home_library_empty);
        }

        if (activity.hasAudioPermission()) {
            textPermissionSummary.setText(R.string.permission_status_granted);
        } else {
            textPermissionSummary.setText(R.string.permission_status_missing);
        }

        if (repository.getSongCount() > 0) {
            textNextStepDesc.setText(getString(R.string.home_card_next_desc_ready, repository.getSongCount()));
        } else {
            textNextStepDesc.setText(R.string.home_card_next_desc_empty);
        }
    }
}
