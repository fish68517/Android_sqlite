package com.localmusic.player.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localmusic.player.databinding.ItemPlaylistSongBinding;
import com.localmusic.player.model.Song;
import com.localmusic.player.util.TimeFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.SongViewHolder> {

    public interface Listener {
        void onSongClick(Song song);

        void onRemoveClick(Song song);
    }

    private final List<Song> songs = new ArrayList<>();
    private final Listener listener;

    public PlaylistSongAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Song> items) {
        songs.clear();
        songs.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistSongBinding binding = ItemPlaylistSongBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new SongViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemPlaylistSongBinding binding;

        SongViewHolder(ItemPlaylistSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Song song) {
            binding.textTitle.setText(song.getTitle());
            binding.textSubtitle.setText(song.getArtist() + " / " + song.getAlbum());
            binding.textDuration.setText(TimeFormatUtils.formatDuration((int) song.getDuration()));
            binding.buttonRemove.setOnClickListener(v -> listener.onRemoveClick(song));
            binding.getRoot().setOnClickListener(v -> listener.onSongClick(song));
        }
    }
}
