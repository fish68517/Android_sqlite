package com.example.xiaoshuo.models;

public class Chapter {
    private int id;
    private int bookId;
    private String title;
    private String content;
    private int chapterNumber;
    private boolean isPaid;

    public Chapter(int id, int bookId, String title, int chapterNumber) {
        this.id = id;
        this.bookId = bookId;
        this.title = title;
        this.chapterNumber = chapterNumber;
    }

    public Chapter(int id, int bookId, String title, String content, int chapterNumber, boolean isPaid) {
        this.id = id;
        this.bookId = bookId;
        this.title = title;
        this.content = content;
        this.chapterNumber = chapterNumber;
        this.isPaid = isPaid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
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

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
} 