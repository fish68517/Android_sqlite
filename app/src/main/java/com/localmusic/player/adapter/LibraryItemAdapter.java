package com.localmusic.player.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.localmusic.player.databinding.ItemLibraryEntryBinding;
import com.localmusic.player.model.LibraryItem;

import java.util.ArrayList;
import java.util.List;

public class LibraryItemAdapter extends RecyclerView.Adapter<LibraryItemAdapter.LibraryItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(LibraryItem item);
    }

    private final List<LibraryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public LibraryItemAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<LibraryItem> libraryItems) {
        items.clear();
        items.addAll(libraryItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibraryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLibraryEntryBinding binding = ItemLibraryEntryBinding.inflate(inflater, parent, false);
        return new LibraryItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class LibraryItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemLibraryEntryBinding binding;

        LibraryItemViewHolder(ItemLibraryEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LibraryItem item) {
            binding.textTitle.setText(item.getTitle());
            binding.textSubtitle.setText(item.getSubtitle());
            binding.textTrailing.setText(item.getTrailing());
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
