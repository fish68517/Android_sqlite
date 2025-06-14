package com.example.orderfood.model;

public class Post {
    private String username, timestamp, content;
    private int imageRes;

    private int  postId;

    private boolean isFavorite; // 新增收藏状态字段


    @Override
    public String toString() {
        return "Post{" +
                "username='" + username + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", content='" + content + '\'' +
                ", imageRes=" + imageRes +
                ", postId=" + postId +
                ", isFavorite=" + isFavorite +
                '}';
    }

    public Post(String title, String timestamp, String content, int imageResource, int postId, int favorite) {
        this.username = title;
        this.timestamp = timestamp;
        this.content = content;
        this.imageRes = imageResource;
        this.postId = postId;
        this.isFavorite = favorite == 1;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Post(String title, String timestamp, String content, int imageResource, int postId) {
        this.username = title;
        this.timestamp = timestamp;
        this.content = content;
        this.imageRes = imageResource;
        this.postId = postId;
    }


    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Post(String username, String timestamp, String content, int imageRes) {
        this.username = username;
        this.timestamp = timestamp;
        this.content = content;
        this.imageRes = imageRes;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getUsername() { return username; }
    public String getTimestamp() { return timestamp; }
    public String getContent() { return content; }
    public int getImageRes() { return imageRes; }
}
