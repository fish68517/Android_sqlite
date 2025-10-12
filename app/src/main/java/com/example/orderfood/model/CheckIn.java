package com.example.orderfood.model;

public class CheckIn {
    private int id;
    private int userId;
    private String checkinDate;

    // Constructors, Getters, and Setters...


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }
}
