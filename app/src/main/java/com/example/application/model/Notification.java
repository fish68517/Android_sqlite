package com.example.application.model;

public class Notification {
    private int id;
    private String title;
    private String content;

    // Constructors
    public Notification() {}

    public Notification(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // toString() is useful for displaying in a ListView
    @Override
    public String toString() {
        return title; // ListView中默认显示标题
    }
}