package com.example.healthdietapp.models;

/**
 * Feedback Model - Represents user feedback
 */
public class Feedback {
    private String feedbackId;
    private String userId;
    private String content;
    private long createdAt;

    public Feedback() {
    }

    public Feedback(String feedbackId, String userId, String content) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
