package com.example.healthdietapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.SearchHistory;
import com.example.healthdietapp.utils.AnimationUtils;

import java.util.List;

/**
 * SearchHistoryAdapter - Adapter for displaying search history in RecyclerView
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder> {

    private List<String> keywords;
    private OnKeywordClickListener listener;

    public interface OnKeywordClickListener {
        void onKeywordClick(String keyword);
    }

    public SearchHistoryAdapter(List<String> keywords, OnKeywordClickListener listener) {
        this.keywords = keywords;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history, parent, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, int position) {
        String keyword = keywords.get(position);
        holder.bind(keyword, listener);
        
        // Apply list item enter animation with staggered delay
        AnimationUtils.applyListItemEnterAnimationWithDelay(holder.itemView, position * 50);
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    public void updateKeywords(List<String> newKeywords) {
        this.keywords = newKeywords;
        notifyDataSetChanged();
    }

    static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView keywordText;

        SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            keywordText = itemView.findViewById(R.id.keywordText);
        }

        void bind(String keyword, OnKeywordClickListener listener) {
            keywordText.setText(keyword);
            itemView.setOnClickListener(v -> listener.onKeywordClick(keyword));
        }
    }
}
