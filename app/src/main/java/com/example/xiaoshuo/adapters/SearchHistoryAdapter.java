package com.example.xiaoshuo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> historyList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String keyword);
    }

    public SearchHistoryAdapter(Context context, List<String> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String keyword = historyList.get(position);
        holder.tvKeyword.setText(keyword);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(keyword);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<String> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvKeyword;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKeyword = itemView.findViewById(R.id.tv_keyword);
        }
    }
} 