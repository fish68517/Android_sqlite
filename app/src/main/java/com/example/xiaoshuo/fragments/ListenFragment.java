package com.example.xiaoshuo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.AudioBookAdapter;
import com.example.xiaoshuo.models.AudioBook;
import com.example.xiaoshuo.utils.BookDataManager;

import java.util.List;

public class ListenFragment extends Fragment {

    private RecyclerView rvAudioBooks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen, container, false);
        
        rvAudioBooks = view.findViewById(R.id.rv_audio_books);
        
        setupAudioBooksRecyclerView();
        
        return view;
    }
    
    private void setupAudioBooksRecyclerView() {
        // 获取有声书数据
        List<AudioBook> audioBooks = BookDataManager.getAudioBooks();
        
        // 设置垂直布局管理器
        rvAudioBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 设置有声书适配器
        AudioBookAdapter audioBookAdapter = new AudioBookAdapter(getContext(), audioBooks);
        rvAudioBooks.setAdapter(audioBookAdapter);
    }
} 