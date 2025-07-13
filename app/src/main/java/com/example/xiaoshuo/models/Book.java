package com.example.xiaoshuo.models;

public class Book {
    private String id;
    private String title;
    private String author;
    private String description;
    private String coverImage; // 书籍封面图片资源ID
    private String category;
    private int chapterCount;
    private boolean isCollected;

    public Book(String id, String title, String author, String description, String coverImage, String category, int chapterCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverImage = coverImage;
        this.category = category;
        this.chapterCount = chapterCount;
        this.isCollected = false;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getCategory() {
        return category;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }
} 