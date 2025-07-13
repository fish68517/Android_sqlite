package com.example.xiaoshuo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.activities.ReaderActivity;
import com.example.xiaoshuo.models.ReadHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReadHistoryAdapter extends RecyclerView.Adapter<ReadHistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<ReadHistory> historyList;
    private OnHistoryItemClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnHistoryItemClickListener {
        void onDeleteClick(ReadHistory history, int position);
    }

    public ReadHistoryAdapter(Context context, List<ReadHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_read_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ReadHistory history = historyList.get(position);
        
        holder.tvBookTitle.setText(history.getBookTitle());
        holder.tvBookAuthor.setText(history.getAuthorName());
        holder.tvLastChapter.setText(history.getLastChapterTitle());
        
        if (history.getReadTime() != null) {
            holder.tvReadTime.setText(dateFormat.format(history.getReadTime()));
        } else {
            holder.tvReadTime.setText("");
        }
        
        // 设置封面图片
        int resId = context.getResources().getIdentifier(
                history.getBookCover(), "drawable", context.getPackageName());
        if (resId != 0) {
            holder.ivBookCover.setImageResource(resId);
        } else {
            holder.ivBookCover.setImageResource(R.drawable.ic_launcher_foreground);
        }
        
        // 设置删除按钮点击事件
        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(history, holder.getAdapterPosition());
            }
        });
        
        // 设置整个条目的点击事件，点击后跳转到阅读页面
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReaderActivity.class);
            intent.putExtra("BOOK_ID", history.getBookId());
            intent.putExtra("book_title", history.getBookTitle());
            intent.putExtra("BOOK_AUTHOR", history.getAuthorName());
            intent.putExtra("BOOK_COVER", history.getBookCover());
            intent.putExtra("chapter_index", history.getLastChapterIndex());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<ReadHistory> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        TextView tvLastChapter;
        TextView tvReadTime;
        ImageView ivDelete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvLastChapter = itemView.findViewById(R.id.tv_last_chapter);
            tvReadTime = itemView.findViewById(R.id.tv_read_time);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
} 