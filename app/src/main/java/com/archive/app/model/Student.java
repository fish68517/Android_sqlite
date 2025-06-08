package com.archive.app.model;

public class Student {
    private long id;
    private String name;
    private String studentId;
    private String phone;
    private String bio;
    private String avatarPath;

    public Student() {
    }

    public Student(long id, String name, String studentId, String phone, String bio, String avatarPath) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.phone = phone;
        this.bio = bio;
        this.avatarPath = avatarPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
} 