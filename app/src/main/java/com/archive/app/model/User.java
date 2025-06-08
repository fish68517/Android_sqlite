package com.archive.app.model;

public class User {
    public long id;
    public String username;
    public String password;


    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

} 