package com.archive.app.model;

import java.io.Serializable;

/**
 * 图书模型类
 * Implements Serializable to allow passing Book objects between activities via Intent.
 */
public class Book implements Serializable {
    private long id;
    private String title;
    private String author;
    private String isbn;
    private String coverImage; // 存储Drawable资源名称 (例如 "java_cover")
    private String description;
    private String publishDate; // 存储格式 "YYYY-MM-DD"
    private long categoryId;
    private String categoryName; // 分类名称，方便显示
    private String createTime; // 创建时间

    public Book() {
    }

    public Book(long id, String title, String author, String isbn, String coverImage, String description, String publishDate, long categoryId, String categoryName, String createTime) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.coverImage = coverImage;
        this.description = description;
        this.publishDate = publishDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
} 