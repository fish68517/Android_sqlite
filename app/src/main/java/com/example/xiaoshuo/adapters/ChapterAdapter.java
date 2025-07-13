package com.example.xiaoshuo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.models.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context context;
    private List<Chapter> chapterList;
    private int currentChapterIndex = -1;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Chapter chapter, int position);
    }

    public ChapterAdapter(Context context, List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
    }

    public void setCurrentChapterIndex(int currentChapterIndex) {
        this.currentChapterIndex = currentChapterIndex;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvChapterTitle.setText(chapter.getTitle());
        
        // 高亮当前章节
        if (position == currentChapterIndex) {
            holder.tvChapterTitle.setTextColor(context.getResources().getColor(R.color.primary));
        } else {
            holder.tvChapterTitle.setTextColor(context.getResources().getColor(R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chapter, position);
                currentChapterIndex = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterTitle;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tv_chapter_title);
        }
    }
} 