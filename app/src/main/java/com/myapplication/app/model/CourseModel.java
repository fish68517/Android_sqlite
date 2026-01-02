package com.myapplication.app.model;

public class CourseModel {
    public int id;
    public String name;
    public String time;
    public String place;

    public CourseModel(int id, String name, String time, String place) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.place = place;
    }
}