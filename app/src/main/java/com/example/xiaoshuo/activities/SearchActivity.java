package com.example.xiaoshuo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.BookAdapter;
import com.example.xiaoshuo.adapters.SearchHistoryAdapter;
import com.example.xiaoshuo.models.Book;
import com.example.xiaoshuo.utils.BookDataManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private TextView tvCancel;
    private RecyclerView rvRecentSearch;
    private ChipGroup chipGroupHotSearch;
    private RecyclerView rvMaleHotSearch;
    private RecyclerView rvFemaleHotSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupListeners();
        loadData();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        tvCancel = findViewById(R.id.tv_cancel);
        rvRecentSearch = findViewById(R.id.rv_recent_search);
        chipGroupHotSearch = findViewById(R.id.chip_group_hot_search);
        rvMaleHotSearch = findViewById(R.id.rv_male_hot_search);
        rvFemaleHotSearch = findViewById(R.id.rv_female_hot_search);
        
        // 设置RecyclerView
        rvRecentSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMaleHotSearch.setLayoutManager(new LinearLayoutManager(this));
        rvFemaleHotSearch.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        tvCancel.setOnClickListener(v -> finish());
        
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
        
        // 设置热门搜索标签点击
        for (int i = 0; i < chipGroupHotSearch.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupHotSearch.getChildAt(i);
            chip.setOnClickListener(v -> {
                etSearch.setText(((Chip) v).getText());
                performSearch(((Chip) v).getText().toString());
            });
        }
    }
    
    private void loadSearchHistory() {
        // 从SharedPreferences获取搜索历史
        SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        Set<String> historySet = sp.getStringSet("history", new HashSet<>());
        
        // 将Set转换为List以便于显示
        List<String> historyList = new ArrayList<>(historySet);
        
        if (historyList.isEmpty()) {
            // 如果没有历史记录，隐藏RecyclerView
            rvRecentSearch.setVisibility(View.GONE);
        } else {
            rvRecentSearch.setVisibility(View.VISIBLE);
            
            // 创建适配器
            SearchHistoryAdapter adapter = new SearchHistoryAdapter(this, historyList);
            rvRecentSearch.setAdapter(adapter);
            
            // 设置点击事件
            adapter.setOnItemClickListener(keyword -> {
                etSearch.setText(keyword);
                performSearch(keyword);
            });
        }
    }

    private void loadData() {
        // 加载最近搜索数据
        loadSearchHistory();
        
        // 加载男生热搜榜数据
        List<Book> maleHotBooks = new ArrayList<>();
        maleHotBooks.add(new Book(UUID.randomUUID().toString(), "战神狂飙", "热门作者", 
                "一个退伍特种兵的热血都市生活", "novel_cover_3", "都市", 325));
        maleHotBooks.add(new Book(UUID.randomUUID().toString(), "那些热血飞扬的日子", "青春回忆", 
                "校园青春的热血故事", "novel_cover_7", "青春", 128));
        maleHotBooks.add(new Book(UUID.randomUUID().toString(), "道界天下", "天道", 
                "修道者的成长历程", "novel_cover_8", "修真", 93));
        maleHotBooks.add(new Book(UUID.randomUUID().toString(), "御天武帝", "剑指苍穹", 
                "武道修炼的巅峰之路", "novel_cover_2", "武侠", 177));
        maleHotBooks.add(new Book(UUID.randomUUID().toString(), "武道凌天", "刀锋", 
                "武者的热血传奇", "novel_cover_9", "武侠", 56));
        
        // 加载女生热搜榜数据
        List<Book> femaleHotBooks = new ArrayList<>();
        femaleHotBooks.add(new Book(UUID.randomUUID().toString(), "天生福妃", "紫霞仙子", 
                "宫廷中的传奇女子", "novel_cover_4", "宫廷", 82));
        femaleHotBooks.add(new Book(UUID.randomUUID().toString(), "虐渣女配", "甜心", 
                "女配逆袭的故事", "novel_cover_5", "现言", 97));
        femaleHotBooks.add(new Book(UUID.randomUUID().toString(), "与校草共度的日子", "青柠", 
                "校园恋爱的甜蜜故事", "novel_cover_12", "校园", 145));
        femaleHotBooks.add(new Book(UUID.randomUUID().toString(), "锦绣传香", "古风", 
                "古代女子的传奇人生", "novel_cover_10", "古言", 118));
        femaleHotBooks.add(new Book(UUID.randomUUID().toString(), "灯下黑", "悬疑女王", 
                "悬疑推理的女性故事", "novel_cover_14", "悬疑", 61));
        
        rvMaleHotSearch.setAdapter(new BookAdapter(this, maleHotBooks));
        rvFemaleHotSearch.setAdapter(new BookAdapter(this, femaleHotBooks));
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            return;
        }
        
        // 保存搜索记录到SharedPreferences
        saveSearchHistory(keyword);
        
        // 跳转到搜索结果页面
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }
    
    private void saveSearchHistory(String keyword) {
        SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        Set<String> history = sp.getStringSet("history", new HashSet<>());
        
        // 创建一个新的HashSet来存储历史记录（因为SharedPreferences返回的是不可修改的Set）
        Set<String> newHistory = new HashSet<>(history);
        
        // 添加新的搜索词（如果已存在，先移除旧的）
        newHistory.remove(keyword);
        newHistory.add(keyword);
        
        // 限制历史记录数量为10个
        if (newHistory.size() > 10) {
            // 移除最早的记录
            List<String> tempList = new ArrayList<>(newHistory);
            tempList.remove(0);
            newHistory = new HashSet<>(tempList);
        }
        
        // 保存新的历史记录
        sp.edit().putStringSet("history", newHistory).apply();
    }
} 