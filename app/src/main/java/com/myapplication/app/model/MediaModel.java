package com.myapplication.app.model;

public class MediaModel {
    public int id;
    public String name;
    public String path;
    public String author;

    public MediaModel(int id, String name, String path, String author) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.author = author;
    }
}