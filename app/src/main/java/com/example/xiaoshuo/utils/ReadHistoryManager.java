package com.example.xiaoshuo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.xiaoshuo.models.Chapter;
import com.example.xiaoshuo.models.ReadHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReadHistoryManager {
    private static final String TAG = "ReadHistoryManager";
    private static final String PREF_NAME_PREFIX = "read_history_pref_";
    private static final String KEY_HISTORY = "key_read_history";
    private static final int MAX_HISTORY_SIZE = 50;

    private static ReadHistoryManager instance;
    private final Context context;
    private final Gson gson;
    private List<ReadHistory> historyList;
    private UserManager userManager;
    private SharedPreferences preferences;

    private ReadHistoryManager(Context context) {
        this.context = context.getApplicationContext();
        gson = new Gson();
        userManager = UserManager.getInstance(context);
        initPreferences();
    }

    public static synchronized ReadHistoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new ReadHistoryManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private void initPreferences() {
        if (userManager.isLoggedIn()) {
            // 使用用户ID作为SharedPreferences名称的一部分，确保每个用户有独立的历史记录
            String prefName = PREF_NAME_PREFIX + userManager.getCurrentUser().getId();
            preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            loadHistory();
        } else {
            // 用户未登录，使用空列表
            historyList = new ArrayList<>();
        }
    }

    private void loadHistory() {
        if (preferences == null) {
            historyList = new ArrayList<>();
            return;
        }
        
        String json = preferences.getString(KEY_HISTORY, "");
        Log.d(TAG, "Loading history from SharedPreferences: " + (json.isEmpty() ? "empty" : "length=" + json.length()));
        
        if (json.isEmpty()) {
            historyList = new ArrayList<>();
        } else {
            try {
                Type type = new TypeToken<List<ReadHistory>>() {}.getType();
                historyList = gson.fromJson(json, type);
                if (historyList == null) {
                    Log.e(TAG, "Deserialized history list is null");
                    historyList = new ArrayList<>();
                } else {
                    Log.d(TAG, "Loaded " + historyList.size() + " history records");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading history", e);
                historyList = new ArrayList<>();
            }
        }
    }

    private void saveHistory() {
        if (preferences == null) {
            // 用户未登录，无法保存
            return;
        }
        
        try {
            String json = gson.toJson(historyList);
            Log.d(TAG, "Saving history to SharedPreferences: " + historyList.size() + " records");
            preferences.edit().putString(KEY_HISTORY, json).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving history", e);
        }
    }
    
    /**
     * 当用户登录状态改变时调用此方法重新初始化
     */
    public void onUserChanged() {
        initPreferences();
    }

    public void addHistory(String bookId, String bookTitle, String bookCover, 
                          String authorName, int chapterIndex, String chapterTitle) {
        if (!userManager.isLoggedIn()) {
            // 用户未登录，不保存阅读历史
            return;
        }
        
        Log.d(TAG, "Adding history: bookId=" + bookId + ", title=" + bookTitle + 
              ", author=" + authorName + ", chapter=" + chapterIndex + ", chapterTitle=" + chapterTitle);
        
        if (bookId == null || bookTitle == null) {
            Log.e(TAG, "Cannot add history with null bookId or bookTitle");
            return;
        }
        
        // 检查是否已存在该书的历史记录
        ReadHistory existingHistory = null;
        for (ReadHistory history : historyList) {
            if (history.getBookId().equals(bookId)) {
                existingHistory = history;
                break;
            }
        }

        if (existingHistory != null) {
            // 更新已有记录
            Log.d(TAG, "Updating existing history record");
            historyList.remove(existingHistory);
            existingHistory.setLastChapterIndex(chapterIndex);
            existingHistory.setLastChapterTitle(chapterTitle);
            existingHistory.setReadTime(new Date());
            historyList.add(0, existingHistory); // 移到列表前面
        } else {
            // 创建新记录
            Log.d(TAG, "Creating new history record");
            String id = UUID.randomUUID().toString();
            ReadHistory newHistory = new ReadHistory(
                    id, bookId, bookTitle, bookCover, authorName, 
                    chapterIndex, chapterTitle, new Date());
            historyList.add(0, newHistory);
        }

        // 限制历史记录数量
        if (historyList.size() > MAX_HISTORY_SIZE) {
            historyList = historyList.subList(0, MAX_HISTORY_SIZE);
        }

        saveHistory();
    }

    public List<ReadHistory> getHistoryList() {
        if (!userManager.isLoggedIn()) {
            // 用户未登录，返回空列表
            return new ArrayList<>();
        }
        
        Log.d(TAG, "Getting history list: " + historyList.size() + " records");
        return new ArrayList<>(historyList);
    }

    public void clearHistory() {
        if (!userManager.isLoggedIn()) {
            return;
        }
        
        Log.d(TAG, "Clearing all history records");
        historyList.clear();
        saveHistory();
    }

    public void removeHistory(String historyId) {
        if (!userManager.isLoggedIn()) {
            return;
        }
        
        Log.d(TAG, "Removing history record: " + historyId);
        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).getId().equals(historyId)) {
                historyList.remove(i);
                saveHistory();
                break;
            }
        }
    }
} 