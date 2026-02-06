package com.example.healthdietapp.models;

/**
 * SearchHistory Model - Represents user search history
 */
public class SearchHistory {
    private String historyId;
    private String userId;
    private String keyword;
    private long searchedAt;

    public SearchHistory() {
    }

    public SearchHistory(String historyId, String userId, String keyword) {
        this.historyId = historyId;
        this.userId = userId;
        this.keyword = keyword;
        this.searchedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(long searchedAt) {
        this.searchedAt = searchedAt;
    }
}
