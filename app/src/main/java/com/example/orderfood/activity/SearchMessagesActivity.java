package com.example.orderfood.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.MessageSearchAdapter;
import com.example.orderfood.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchMessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageSearchAdapter adapter;
    private List<SearchResult> searchResults;
    private DataBaseOpenHelper dbHelper;
    private TextView tvInfo;
    private String query;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_messages);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        query = getIntent().getStringExtra("QUERY");
        if (query == null || query.trim().isEmpty()) {
            Toast.makeText(this, "无效的搜索词", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("搜索: " + query);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sessionPrefs = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
        currentUserId = sessionPrefs.getInt("CURRENT_USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "用户状态异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DataBaseOpenHelper(this);
        initViews();
        performSearch();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_search_results);
        tvInfo = findViewById(R.id.tv_search_results_info);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();
        adapter = new MessageSearchAdapter(searchResults, query);
        recyclerView.setAdapter(adapter);
    }

    private void performSearch() {
        Cursor cursor = dbHelper.searchAllMessages(currentUserId, query);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        SearchResult result = new SearchResult();
                        result.setMessageId(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_MESSAGE_ID)));
                        result.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_CONTENT)));
                        result.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_TIMESTAMP)));
                        result.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_SENDER_ID)));
                        result.setSenderNickname(cursor.getString(cursor.getColumnIndexOrThrow("sender_nickname")));
                        result.setConversationName(cursor.getString(cursor.getColumnIndexOrThrow("conversation_name")));
                        result.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_RECEIVER_ID)));
                        result.setGroupId(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseOpenHelper.COLUMN_GROUP_ID)));
                        searchResults.add(result);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        tvInfo.setText("聊天记录");
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "未找到相关消息", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
} 