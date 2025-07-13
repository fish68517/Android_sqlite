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
import com.example.xiaoshuo.activities.BookDetailActivity;
import com.example.xiaoshuo.models.Book;
import com.example.xiaoshuo.models.Ranking;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private Context context;
    private List<Ranking> rankings;

    public RankingAdapter(Context context, List<Ranking> rankings) {
        this.context = context;
        this.rankings = rankings;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Ranking ranking = rankings.get(position);
        holder.tvRankingTitle.setText(ranking.getTitle());
        holder.tvRankingDesc.setText(ranking.getDescription());
        
        List<Book> books = ranking.getBooks();
        if (books.size() > 0) {
            Book book1 = books.get(0);
            holder.tvBook1Title.setText(book1.getTitle());
            holder.tvBook1Author.setText(book1.getAuthor());
            
            int resId = context.getResources().getIdentifier(
                    book1.getCoverImage(), "drawable", context.getPackageName());
            holder.ivBook1Cover.setImageResource(resId);
            
            holder.vBook1.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book1.getId());
                intent.putExtra("BOOK_TITLE", book1.getTitle());
                intent.putExtra("BOOK_AUTHOR", book1.getAuthor());
                intent.putExtra("BOOK_DESCRIPTION", book1.getDescription());
                intent.putExtra("BOOK_COVER", book1.getCoverImage());
                intent.putExtra("BOOK_CATEGORY", book1.getCategory());
                intent.putExtra("BOOK_CHAPTER_COUNT", book1.getChapterCount());
                context.startActivity(intent);
            });
        }
        
        if (books.size() > 1) {
            Book book2 = books.get(1);
            holder.tvBook2Title.setText(book2.getTitle());
            holder.tvBook2Author.setText(book2.getAuthor());

            int resId = context.getResources().getIdentifier(
                    book2.getCoverImage(), "drawable", context.getPackageName());
            holder.ivBook2Cover.setImageResource(resId);
            
            holder.vBook2.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book2.getId());
                intent.putExtra("BOOK_TITLE", book2.getTitle());
                intent.putExtra("BOOK_AUTHOR", book2.getAuthor());
                intent.putExtra("BOOK_DESCRIPTION", book2.getDescription());
                intent.putExtra("BOOK_COVER", book2.getCoverImage());
                intent.putExtra("BOOK_CATEGORY", book2.getCategory());
                intent.putExtra("BOOK_CHAPTER_COUNT", book2.getChapterCount());
                context.startActivity(intent);
            });
        }
        
        if (books.size() > 2) {
            Book book3 = books.get(2);
            holder.tvBook3Title.setText(book3.getTitle());
            holder.tvBook3Author.setText(book3.getAuthor());
            
            int resId = context.getResources().getIdentifier(
                    book3.getCoverImage(), "drawable", context.getPackageName());
            holder.ivBook3Cover.setImageResource(resId);
            
            holder.vBook3.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book3.getId());
                intent.putExtra("BOOK_TITLE", book3.getTitle());
                intent.putExtra("BOOK_AUTHOR", book3.getAuthor());
                intent.putExtra("BOOK_DESCRIPTION", book3.getDescription());
                intent.putExtra("BOOK_COVER", book3.getCoverImage());
                intent.putExtra("BOOK_CATEGORY", book3.getCategory());
                intent.putExtra("BOOK_CHAPTER_COUNT", book3.getChapterCount());
                context.startActivity(intent);
            });
        }
        
        holder.tvMoreRanking.setOnClickListener(v -> {
            // 可以跳转到完整榜单页面
            // TODO: 实现跳转到完整榜单页面
        });
    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankingTitle, tvRankingDesc, tvMoreRanking;
        View vBook1, vBook2, vBook3;
        ImageView ivBook1Cover, ivBook2Cover, ivBook3Cover;
        TextView tvBook1Title, tvBook2Title, tvBook3Title;
        TextView tvBook1Author, tvBook2Author, tvBook3Author;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRankingTitle = itemView.findViewById(R.id.tv_ranking_title);
            tvRankingDesc = itemView.findViewById(R.id.tv_ranking_desc);
            tvMoreRanking = itemView.findViewById(R.id.tv_more_ranking);
            
            vBook1 = itemView.findViewById(R.id.layout_book1);
            vBook2 = itemView.findViewById(R.id.layout_book2);
            vBook3 = itemView.findViewById(R.id.layout_book3);
            
            ivBook1Cover = itemView.findViewById(R.id.iv_book1_cover);
            ivBook2Cover = itemView.findViewById(R.id.iv_book2_cover);
            ivBook3Cover = itemView.findViewById(R.id.iv_book3_cover);
            
            tvBook1Title = itemView.findViewById(R.id.tv_book1_title);
            tvBook2Title = itemView.findViewById(R.id.tv_book2_title);
            tvBook3Title = itemView.findViewById(R.id.tv_book3_title);
            
            tvBook1Author = itemView.findViewById(R.id.tv_book1_author);
            tvBook2Author = itemView.findViewById(R.id.tv_book2_author);
            tvBook3Author = itemView.findViewById(R.id.tv_book3_author);
        }
    }
} 