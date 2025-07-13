package com.example.xiaoshuo.models;

import java.util.List;

public class Ranking {
    private String id;
    private String title;
    private String description;
    private List<Book> books;

    public Ranking(String id, String title, String description, List<Book> books) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.books = books;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
} 