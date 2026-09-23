package com.localmusic.player.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localmusic.player.R;
import com.localmusic.player.databinding.ItemUserPlaylistBinding;
import com.localmusic.player.model.UserPlaylist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.PlaylistViewHolder> {

    public interface OnPlaylistClickListener {
        void onPlaylistClick(UserPlaylist playlist);
    }

    private final List<UserPlaylist> playlists = new ArrayList<>();
    private final OnPlaylistClickListener listener;

    public UserPlaylistAdapter(OnPlaylistClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<UserPlaylist> items) {
        playlists.clear();
        playlists.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserPlaylistBinding binding = ItemUserPlaylistBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new PlaylistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.bind(playlists.get(position));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private final ItemUserPlaylistBinding binding;

        PlaylistViewHolder(ItemUserPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserPlaylist playlist) {
            binding.textPlaylistName.setText(playlist.getName());
            binding.textPlaylistCount.setText(binding.getRoot().getContext().getString(
                    R.string.playlist_song_count,
                    playlist.getSongIds().size()));
            binding.textPlaylistUpdated.setText(binding.getRoot().getContext().getString(
                    R.string.playlist_updated_time,
                    new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                            .format(new Date(playlist.getUpdatedAt()))));
            binding.getRoot().setOnClickListener(v -> listener.onPlaylistClick(playlist));
        }
    }
}
