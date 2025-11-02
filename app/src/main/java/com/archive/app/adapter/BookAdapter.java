package com.archive.app.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.archive.app.model.Book;
// LoginActivity.java 使用 R.layout.activity_login 等，其包名为 com.example.myapplication
// 因此这里的 R 文件也应该是同一个包
import com.example.myapplication.R;

import java.util.List;

/**
 * 书籍列表的 RecyclerView 适配器
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private Context context;
    private OnBookItemClickListener listener;

    /**
     * 列表项点击事件监听器接口
     */
    public interface OnBookItemClickListener {
        void onViewClick(Book book);      // 查看按钮点击
        void onEditClick(Book book);      // 编辑按钮点击
        void onDeleteClick(Book book);    // 删除按钮点击
    }

    public BookAdapter(Context context, List<Book> bookList, OnBookItemClickListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
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
        if (book == null) return; // 基本的null检查

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(String.format("作者: %s", book.getAuthor()));
        holder.tvIsbn.setText(String.format("ISBN: %s", book.getIsbn()));
        holder.tvPublishDate.setText(String.format("出版日期: %s", book.getPublishDate()));
        holder.tvDescription.setText(book.getDescription() != null && !book.getDescription().isEmpty() ? book.getDescription() : "暂无简介");




        // 加载封面图片
        // --- 2. 修改后的图片加载逻辑 ---
        String coverImageString = book.getCoverImage();
        if (!TextUtils.isEmpty(coverImageString)) {
            // 判断是 URI 还是 drawable 资源名
            if (coverImageString.startsWith("content://") || coverImageString.startsWith("file://")) {
                // 这是新的 URI 路径
                try {
                    Uri imageUri = Uri.parse(coverImageString);
                    holder.ivCover.setImageURI(imageUri);
                } catch (Exception e) {
                    // 如果URI解析或加载失败（例如文件已被删除），显示默认图片
                    Log.e("BookAdapter", "加载图片URI失败: " + coverImageString, e);
                    holder.ivCover.setImageResource(R.drawable.default_book_icon);
                }
            } else {
                // 这是旧的 drawable 资源名 (为了兼容旧数据)
                int imageResId = context.getResources().getIdentifier(
                        coverImageString.toLowerCase(), "drawable", context.getPackageName());
                if (imageResId != 0) {
                    holder.ivCover.setImageResource(imageResId);
                } else {
                    holder.ivCover.setImageResource(R.drawable.default_book_icon);
                }
            }
        } else {
            // 如果没有图片信息，使用默认图片
            holder.ivCover.setImageResource(R.drawable.default_book_icon);
        }
        // --- 图片加载逻辑修改结束 ---
        // 设置按钮的点击监听
        holder.btnView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewClick(book);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(book);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    /**
     * 更新适配器中的数据列表
     * @param newBookList 新的书籍列表
     */
    public void setBooks(List<Book> newBookList) {
        this.bookList = newBookList;
        notifyDataSetChanged(); // 对于简单列表，这个方法足够了。对于大数据集，考虑使用DiffUtil。
    }

    /**
     * ViewHolder 类，用于缓存列表项的视图组件
     */
    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvIsbn, tvPublishDate, tvDescription;
        Button btnView, btnEdit, btnDelete;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_book_cover);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvAuthor = itemView.findViewById(R.id.tv_book_author);
            tvIsbn = itemView.findViewById(R.id.tv_book_isbn);
            tvPublishDate = itemView.findViewById(R.id.tv_book_publish_date);
            tvDescription = itemView.findViewById(R.id.tv_book_description_item);
            btnView = itemView.findViewById(R.id.btn_view_book);
            btnEdit = itemView.findViewById(R.id.btn_edit_book);
            btnDelete = itemView.findViewById(R.id.btn_delete_book);
        }
    }
} 