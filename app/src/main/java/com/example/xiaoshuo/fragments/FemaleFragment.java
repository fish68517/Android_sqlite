package com.example.xiaoshuo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.BookAdapter;
import com.example.xiaoshuo.models.Book;
import com.example.xiaoshuo.utils.BookDataManager;

import java.util.List;

public class FemaleFragment extends Fragment {

    private RecyclerView rvBooks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_female, container, false);
        
        rvBooks = view.findViewById(R.id.rv_books);
        setupBooksRecyclerView();
        
        return view;
    }
    
    private void setupBooksRecyclerView() {
        // 获取女生频道的书籍数据
        List<Book> books = BookDataManager.getFemaleBooks();
        
        // 设置网格布局管理器（每行显示3本书）
        rvBooks.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        // 设置书籍适配器
        BookAdapter bookAdapter = new BookAdapter(getContext(), books);
        rvBooks.setAdapter(bookAdapter);
    }
} 