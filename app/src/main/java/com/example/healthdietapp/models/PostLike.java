package com.example.healthdietapp.models;

/**
 * PostLike Model - Represents a user's like on a post
 */
public class PostLike {
    private String likeId;
    private String userId;
    private String postId;
    private long likedAt;

    public PostLike() {
    }

    public PostLike(String likeId, String userId, String postId) {
        this.likeId = likeId;
        this.userId = userId;
        this.postId = postId;
        this.likedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
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

    public long getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(long likedAt) {
        this.likedAt = likedAt;
    }
}
