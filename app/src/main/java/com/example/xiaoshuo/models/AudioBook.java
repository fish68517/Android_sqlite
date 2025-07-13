package com.example.xiaoshuo.models;

public class AudioBook {
    private String id;
    private String title;
    private String author;
    private String description;
    private String coverImage; // 书籍封面图片资源ID
    private String category;
    private int episodeCount;
    private boolean isCollected;
    private String narrator; // 有声书特有属性：朗读者

    public AudioBook(String id, String title, String author, String description, String coverImage, 
                    String category, int episodeCount, String narrator) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverImage = coverImage;
        this.category = category;
        this.episodeCount = episodeCount;
        this.isCollected = false;
        this.narrator = narrator;
    }

    // Getters and setters
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

    public int getEpisodeCount() {
        return episodeCount;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public String getNarrator() {
        return narrator;
    }
} 