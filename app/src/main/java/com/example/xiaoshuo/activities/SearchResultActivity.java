package com.example.xiaoshuo.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.BookAdapter;
import com.example.xiaoshuo.models.Book;
import com.example.xiaoshuo.utils.BookDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvKeyword;
    private TextView tvResultCount;
    private RecyclerView rvSearchResult;
    private TextView tvEmptyResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        initViews();
        setupListeners();

        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            searchBooks(keyword);
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvKeyword = findViewById(R.id.tv_keyword);
        tvResultCount = findViewById(R.id.tv_result_count);
        rvSearchResult = findViewById(R.id.rv_search_result);
        tvEmptyResult = findViewById(R.id.tv_empty_result);

        // 设置网格布局，每行3列
        rvSearchResult.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void searchBooks(String keyword) {
        // 设置搜索关键词
        tvKeyword.setText(keyword);

        // 获取所有书籍数据
        List<Book> allBooks = new ArrayList<>();
        allBooks.addAll(BookDataManager.getMaleBooks());
        allBooks.addAll(BookDataManager.getFemaleBooks());

        // 根据关键词过滤书籍
        List<Book> searchResult = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getTitle().toLowerCase(Locale.getDefault()).contains(keyword.toLowerCase(Locale.getDefault())) ||
                book.getAuthor().toLowerCase(Locale.getDefault()).contains(keyword.toLowerCase(Locale.getDefault())) ||
                book.getCategory().toLowerCase(Locale.getDefault()).contains(keyword.toLowerCase(Locale.getDefault()))) {
                searchResult.add(book);
            }
        }

        // 显示搜索结果
        if (searchResult.isEmpty()) {
            tvResultCount.setVisibility(View.GONE);
            tvEmptyResult.setVisibility(View.VISIBLE);
            rvSearchResult.setVisibility(View.GONE);
        } else {
            tvResultCount.setVisibility(View.VISIBLE);
            tvResultCount.setText(String.format("找到 %d 本相关书籍", searchResult.size()));
            tvEmptyResult.setVisibility(View.GONE);
            rvSearchResult.setVisibility(View.VISIBLE);
            
            BookAdapter adapter = new BookAdapter(this, searchResult);
            rvSearchResult.setAdapter(adapter);
        }
    }
} 