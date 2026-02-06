package com.example.healthdietapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.healthdietapp.models.SearchHistory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SearchHistoryDAO - Data Access Object for SearchHistory operations
 * Handles search history CRUD operations
 */
public class SearchHistoryDAO {
    private DatabaseHelper dbHelper;

    public SearchHistoryDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Add search history entry
     */
    public boolean addSearchHistory(SearchHistory history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (history.getHistoryId() == null) {
                history.setHistoryId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("history_id", history.getHistoryId());
            values.put("user_id", history.getUserId());
            values.put("keyword", history.getKeyword());
            values.put("searched_at", history.getSearchedAt());
            
            long result = db.insert("search_history", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get search history entry by ID
     */
    public SearchHistory getSearchHistoryById(String historyId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("search_history", null, "history_id = ?", 
                    new String[]{historyId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                SearchHistory history = cursorToSearchHistory(cursor);
                cursor.close();
                return history;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get all search history for a user
     */
    public List<SearchHistory> getUserSearchHistory(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<SearchHistory> histories = new ArrayList<>();
        try {
            Cursor cursor = db.query("search_history", null, "user_id = ?", 
                    new String[]{userId}, null, null, "searched_at DESC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    histories.add(cursorToSearchHistory(cursor));
                }
                cursor.close();
            }
            return histories;
        } finally {
            db.close();
        }
    }

    /**
     * Get recent search history for a user (limit to specified count)
     */
    public List<SearchHistory> getRecentSearchHistory(String userId, int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<SearchHistory> histories = new ArrayList<>();
        try {
            Cursor cursor = db.query("search_history", null, "user_id = ?", 
                    new String[]{userId}, null, null, "searched_at DESC LIMIT " + limit);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    histories.add(cursorToSearchHistory(cursor));
                }
                cursor.close();
            }
            return histories;
        } finally {
            db.close();
        }
    }

    /**
     * Get unique keywords from search history for a user
     */
    public List<String> getUniqueSearchKeywords(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> keywords = new ArrayList<>();
        try {
            Cursor cursor = db.query(true, "search_history", new String[]{"keyword"}, 
                    "user_id = ?", new String[]{userId}, null, null, 
                    "searched_at DESC", null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    keywords.add(cursor.getString(cursor.getColumnIndexOrThrow("keyword")));
                }
                cursor.close();
            }
            return keywords;
        } finally {
            db.close();
        }
    }

    /**
     * Update search history entry
     */
    public boolean updateSearchHistory(SearchHistory history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("keyword", history.getKeyword());
            values.put("searched_at", history.getSearchedAt());
            
            int result = db.update("search_history", values, "history_id = ?", 
                    new String[]{history.getHistoryId()});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete search history entry
     */
    public boolean deleteSearchHistory(String historyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("search_history", "history_id = ?", 
                    new String[]{historyId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete all search history for a user
     */
    public boolean deleteAllUserSearchHistory(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("search_history", "user_id = ?", 
                    new String[]{userId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete search history older than specified date
     */
    public boolean deleteOldSearchHistory(String userId, long beforeTimestamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("search_history", 
                    "user_id = ? AND searched_at < ?", 
                    new String[]{userId, String.valueOf(beforeTimestamp)});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Convert cursor to SearchHistory object
     */
    private SearchHistory cursorToSearchHistory(Cursor cursor) {
        SearchHistory history = new SearchHistory();
        history.setHistoryId(cursor.getString(cursor.getColumnIndexOrThrow("history_id")));
        history.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
        history.setKeyword(cursor.getString(cursor.getColumnIndexOrThrow("keyword")));
        history.setSearchedAt(cursor.getLong(cursor.getColumnIndexOrThrow("searched_at")));
        return history;
    }
}
