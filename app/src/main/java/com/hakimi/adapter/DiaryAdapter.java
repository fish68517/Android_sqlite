package com.hakimi.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hakimi.R;
import com.hakimi.model.Diary;

import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<Diary> diaries;

    public DiaryAdapter(List<Diary> diaries) {
        this.diaries = diaries;
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries == null ? new ArrayList<>() : diaries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = diaries.get(position);
        holder.tvContent.setText(diary.getContent());
        holder.tvTime.setText(formatTime(diary.getCreatedAt()));
        holder.tvMood.setText(getMoodLabel(diary.getMood()));
    }

    @Override
    public int getItemCount() {
        return diaries == null ? 0 : diaries.size();
    }

    private String formatTime(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("T", " ");
    }

    private String getMoodLabel(Integer mood) {
        if (mood == null) {
            return "";
        }
        if (mood == 1) {
            return "\u4e0d\u5f00\u5fc3";
        }
        if (mood == 2) {
            return "\u4e00\u822c";
        }
        return "\u5f00\u5fc3";
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvMood;
        TextView tvTime;
        TextView tvContent;

        DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMood = itemView.findViewById(R.id.tv_diary_mood);
            tvTime = itemView.findViewById(R.id.tv_diary_time);
            tvContent = itemView.findViewById(R.id.tv_diary_content);
        }
    }
}
