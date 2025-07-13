package com.example.xiaoshuo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.BookAdapter;
import com.example.xiaoshuo.models.Book;
import com.example.xiaoshuo.utils.BookDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookshelfFragment extends Fragment {

    private RecyclerView rvBooks;
    private TextView tvReadMinutes;
    private Button btnGetReward;
    private BookAdapter bookAdapter;
    private List<Book> bookList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadData();
        
        return view;
    }

    private void initViews(View view) {
        rvBooks = view.findViewById(R.id.rv_books);
        tvReadMinutes = view.findViewById(R.id.tv_read_minutes);
        btnGetReward = view.findViewById(R.id.btn_get_reward);
        
        btnGetReward.setOnClickListener(v -> {
            // 处理领取奖励逻辑
        });
    }

    private void setupRecyclerView() {
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(getContext(), bookList);
        rvBooks.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvBooks.setAdapter(bookAdapter);
    }

    private void loadData() {
        // 从BookDataManager获取书籍数据
        bookList.clear();
        bookList.addAll(BookDataManager.getMaleBooks().subList(0, 4)); // 只显示前4本书
        bookAdapter.notifyDataSetChanged();
    }
} 