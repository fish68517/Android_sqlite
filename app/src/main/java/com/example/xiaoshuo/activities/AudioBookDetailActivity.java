package com.example.xiaoshuo.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;

public class AudioBookDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvCategory;
    private TextView tvDescription;
    private TextView tvNarrator;
    private Button btnListen;
    private Button btnCollect;
    private RecyclerView rvRecommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiobook_detail);

        // 简化实现，仅初始化工具栏
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("有声书详情");
        toolbar.setNavigationOnClickListener(v -> finish());

        // 在实际项目中应该完成界面初始化和数据加载
        Toast.makeText(this, "有声书详情页面", Toast.LENGTH_SHORT).show();
    }
} 