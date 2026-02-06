package com.example.healthdietapp.models;

/**
 * PostCollection Model - Represents a user's collection (bookmark) of a post
 */
public class PostCollection {
    private String collectionId;
    private String userId;
    private String postId;
    private long collectedAt;

    public PostCollection() {
    }

    public PostCollection(String collectionId, String userId, String postId) {
        this.collectionId = collectionId;
        this.userId = userId;
        this.postId = postId;
        this.collectedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(long collectedAt) {
        this.collectedAt = collectedAt;
    }
}
