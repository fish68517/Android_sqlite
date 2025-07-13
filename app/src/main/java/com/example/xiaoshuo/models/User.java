package com.example.xiaoshuo.models;

import java.util.List;

public class User {
    private int id;
    private String username;
    private String password;
    private String phoneNumber;
    private String avatarUrl;
    private boolean isPremium;
    private int coins;
    private int readTime;
    private List<Book> favoriteBooks;
    private List<Book> readHistory;
    private String gender; // 添加性别字段
    
    public User() {
    }

    public User(int id, String username, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }

    public List<Book> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<Book> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    public List<Book> getReadHistory() {
        return readHistory;
    }

    public void setReadHistory(List<Book> readHistory) {
        this.readHistory = readHistory;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
} 