package com.example.xiaoshuo.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.ReadHistoryAdapter;
import com.example.xiaoshuo.models.ReadHistory;
import com.example.xiaoshuo.utils.ReadHistoryManager;

import java.util.List;

public class ReadHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvHistory;
    private TextView tvEmptyView;
    private Button btnClearHistory;

    private ReadHistoryManager historyManager;
    private ReadHistoryAdapter adapter;
    private List<ReadHistory> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_history);

        historyManager = ReadHistoryManager.getInstance(this);
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadHistoryData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvHistory = findViewById(R.id.rv_history);
        tvEmptyView = findViewById(R.id.tv_empty_view);
        btnClearHistory = findViewById(R.id.btn_clear_history);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = historyManager.getHistoryList();
        adapter = new ReadHistoryAdapter(this, historyList);
        rvHistory.setAdapter(adapter);

        adapter.setOnHistoryItemClickListener((history, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("删除记录")
                    .setMessage("确定删除该阅读记录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        historyManager.removeHistory(history.getId());
                        loadHistoryData();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void setupListeners() {
        btnClearHistory.setOnClickListener(v -> {
            if (historyList.isEmpty()) {
                return;
            }
            
            new AlertDialog.Builder(this)
                    .setTitle("清空历史")
                    .setMessage("确定清空所有阅读历史吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        historyManager.clearHistory();
                        loadHistoryData();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void loadHistoryData() {
        historyList = historyManager.getHistoryList();
        adapter.updateData(historyList);
        
        if (historyList.isEmpty()) {
            tvEmptyView.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
            btnClearHistory.setEnabled(false);
        } else {
            tvEmptyView.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            btnClearHistory.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryData(); // 每次页面恢复时更新数据
    }
} 