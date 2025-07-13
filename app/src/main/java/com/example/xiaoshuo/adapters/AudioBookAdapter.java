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
import com.example.xiaoshuo.activities.AudioBookDetailActivity;
import com.example.xiaoshuo.models.AudioBook;

import java.util.List;

public class AudioBookAdapter extends RecyclerView.Adapter<AudioBookAdapter.AudioBookViewHolder> {

    private Context context;
    private List<AudioBook> audioBookList;

    public AudioBookAdapter(Context context, List<AudioBook> audioBookList) {
        this.context = context;
        this.audioBookList = audioBookList;
    }

    @NonNull
    @Override
    public AudioBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio_book, parent, false);
        return new AudioBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioBookViewHolder holder, int position) {
        AudioBook audioBook = audioBookList.get(position);
        holder.titleTextView.setText(audioBook.getTitle());
        holder.narratorTextView.setText("播音：" + audioBook.getNarrator());
        holder.episodeCountTextView.setText(audioBook.getEpisodeCount() + "集");
        
        // 设置封面图片
        String coverImageName = audioBook.getCoverImage();
        int resourceId = context.getResources().getIdentifier(
                coverImageName, "drawable", context.getPackageName());
        holder.coverImageView.setImageResource(resourceId);
        
        // 点击事件，打开有声书详情
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AudioBookDetailActivity.class);
            intent.putExtra("AUDIOBOOK_ID", audioBook.getId());
            intent.putExtra("AUDIOBOOK_TITLE", audioBook.getTitle());
            intent.putExtra("AUDIOBOOK_AUTHOR", audioBook.getAuthor());
            intent.putExtra("AUDIOBOOK_DESCRIPTION", audioBook.getDescription());
            intent.putExtra("AUDIOBOOK_COVER", audioBook.getCoverImage());
            intent.putExtra("AUDIOBOOK_CATEGORY", audioBook.getCategory());
            intent.putExtra("AUDIOBOOK_EPISODE_COUNT", audioBook.getEpisodeCount());
            intent.putExtra("AUDIOBOOK_NARRATOR", audioBook.getNarrator());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return audioBookList.size();
    }

    public static class AudioBookViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView narratorTextView;
        TextView episodeCountTextView;

        public AudioBookViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.audio_book_cover);
            titleTextView = itemView.findViewById(R.id.audio_book_title);
            narratorTextView = itemView.findViewById(R.id.audio_book_narrator);
            episodeCountTextView = itemView.findViewById(R.id.audio_book_episode_count);
        }
    }
} 