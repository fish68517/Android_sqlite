package com.example.healthdietapp.models;

/**
 * UserFollow Model - Represents a user following another user
 */
public class UserFollow {
    private String followId;
    private String followerId;
    private String followeeId;
    private long followedAt;

    public UserFollow() {
    }

    public UserFollow(String followId, String followerId, String followeeId) {
        this.followId = followId;
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.followedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(String followeeId) {
        this.followeeId = followeeId;
    }

    public long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(long followedAt) {
        this.followedAt = followedAt;
    }
}
