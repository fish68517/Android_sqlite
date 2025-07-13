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

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        
        // 设置封面图片
        String coverImageName = book.getCoverImage();
        int resourceId = context.getResources().getIdentifier(
                coverImageName, "drawable", context.getPackageName());
        holder.coverImageView.setImageResource(resourceId);
        
        // 点击事件，打开书籍详情
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("BOOK_ID", book.getId());
            intent.putExtra("BOOK_TITLE", book.getTitle());
            intent.putExtra("BOOK_AUTHOR", book.getAuthor());
            intent.putExtra("BOOK_DESCRIPTION", book.getDescription());
            intent.putExtra("BOOK_COVER", book.getCoverImage());
            intent.putExtra("BOOK_CATEGORY", book.getCategory());
            intent.putExtra("BOOK_CHAPTER_COUNT", book.getChapterCount());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView authorTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.book_cover);
            titleTextView = itemView.findViewById(R.id.book_title);
            authorTextView = itemView.findViewById(R.id.book_author);
        }
    }
} 