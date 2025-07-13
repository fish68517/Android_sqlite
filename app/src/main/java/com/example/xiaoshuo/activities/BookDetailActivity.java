package com.example.xiaoshuo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.BookAdapter;
import com.example.xiaoshuo.models.Book;
import com.example.xiaoshuo.utils.BookDataManager;

import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvCategory;
    private TextView tvDescription;
    private Button btnRead;
    private Button btnCollect;
    private RecyclerView rvRecommend;

    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookDescription;
    private String bookCover;
    private String bookCategory;
    private int bookChapterCount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // 获取传递的书籍数据
        Intent intent = getIntent();
        bookId = intent.getStringExtra("BOOK_ID");
        bookTitle = intent.getStringExtra("BOOK_TITLE");
        bookAuthor = intent.getStringExtra("BOOK_AUTHOR");
        bookDescription = intent.getStringExtra("BOOK_DESCRIPTION");
        bookCover = intent.getStringExtra("BOOK_COVER");
        bookCategory = intent.getStringExtra("BOOK_CATEGORY");
        bookChapterCount = intent.getIntExtra("BOOK_CHAPTER_COUNT", 0);

        initViews();
        setupListeners();
        loadBookData();
        loadRecommendBooks();
    }
    
    private void initViews() {
        // 初始化工具栏
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bookTitle);
        toolbar.setNavigationOnClickListener(v -> finish());
        
        // 初始化视图
        ivCover = findViewById(R.id.iv_cover);
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        tvCategory = findViewById(R.id.tv_category);
        tvDescription = findViewById(R.id.tv_description);
        btnRead = findViewById(R.id.btn_read);
        btnCollect = findViewById(R.id.btn_collect);
        rvRecommend = findViewById(R.id.rv_recommend);
        
        // 设置推荐列表
        rvRecommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    
    private void setupListeners() {
        // 阅读按钮点击事件
        btnRead.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, ReaderActivity.class);
            intent.putExtra("BOOK_ID", bookId);
            intent.putExtra("book_title", bookTitle);
            intent.putExtra("BOOK_AUTHOR", bookAuthor);
            intent.putExtra("BOOK_COVER", bookCover);
            intent.putExtra("chapter_index", 0); // 默认从第一章开始阅读
            startActivity(intent);
        });
        
        // 收藏按钮点击事件
        btnCollect.setOnClickListener(v -> {
            // 模拟收藏功能
            btnCollect.setText(btnCollect.getText().toString().equals("收藏") ? "已收藏" : "收藏");
            Toast.makeText(BookDetailActivity.this, 
                    btnCollect.getText().toString().equals("收藏") ? "取消收藏成功" : "收藏成功", 
                    Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadBookData() {
        // 设置书籍封面
        int coverResourceId = getResources().getIdentifier(
                bookCover, "drawable", getPackageName());
        ivCover.setImageResource(coverResourceId);
        
        // 设置书籍信息
        tvTitle.setText(bookTitle);
        tvAuthor.setText("作者：" + bookAuthor);
        tvCategory.setText("分类：" + bookCategory);
        tvDescription.setText(bookDescription);
    }
    
    private void loadRecommendBooks() {
        // 根据当前书籍分类获取推荐书籍
        List<Book> recommendBooks;
        if (bookCategory.equals("玄幻") || bookCategory.equals("武侠") || 
                bookCategory.equals("仙侠") || bookCategory.equals("都市") || 
                bookCategory.equals("游戏") || bookCategory.equals("科幻")) {
            recommendBooks = BookDataManager.getMaleBooks();
        } else {
            recommendBooks = BookDataManager.getFemaleBooks();
        }
        
        // 过滤掉当前书籍
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : recommendBooks) {
            if (!book.getTitle().equals(bookTitle)) {
                filteredBooks.add(book);
            }
            if (filteredBooks.size() >= 5) {
                break; // 最多显示5本推荐书籍
            }
        }
        
        // 设置推荐书籍适配器
        BookAdapter adapter = new BookAdapter(this, filteredBooks);
        rvRecommend.setAdapter(adapter);
    }
} 