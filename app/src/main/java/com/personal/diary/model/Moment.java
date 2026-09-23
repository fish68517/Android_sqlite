package com.personal.diary.model;

public class Moment {
    public long id;
    public long userId;
    public String author;
    public String content;
    public String imagePath;
    public byte[] imageData;
    public String createdAt;
    public int likeCount;
    public int favoriteCount;
    public boolean liked;
    public boolean favorited;
}
