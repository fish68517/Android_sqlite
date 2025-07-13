package com.example.xiaoshuo.models;

import java.util.Date;

public class ReadHistory {
    private String id;
    private String bookId;
    private String bookTitle;
    private String bookCover;
    private String authorName;
    private int lastChapterIndex;
    private String lastChapterTitle;
    private Date readTime;

    public ReadHistory(String id, String bookId, String bookTitle, String bookCover, 
                      String authorName, int lastChapterIndex, String lastChapterTitle, Date readTime) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookCover = bookCover;
        this.authorName = authorName;
        this.lastChapterIndex = lastChapterIndex;
        this.lastChapterTitle = lastChapterTitle;
        this.readTime = readTime;
    }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookCover() {
        return bookCover;
    }

    public String getAuthorName() {
        return authorName;
    }

    public int getLastChapterIndex() {
        return lastChapterIndex;
    }

    public String getLastChapterTitle() {
        return lastChapterTitle;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setLastChapterIndex(int lastChapterIndex) {
        this.lastChapterIndex = lastChapterIndex;
    }

    public void setLastChapterTitle(String lastChapterTitle) {
        this.lastChapterTitle = lastChapterTitle;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
} 