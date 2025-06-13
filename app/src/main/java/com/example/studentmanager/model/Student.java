package com.example.studentmanager.model;

/**
 * 学生数据模型类
 */
public class Student {
    private int studentId;
    private String name;
    private String gender;
    private int classId;
    private String admissionDate;
    private String graduationDate;
    private String status;
    private String username;
    private String password;
    private int isAdmin;
    private String createTime;

    // 构造函数
    public Student() {}

    public Student(String name, String gender, int classId, String admissionDate, 
                  String status, String username, String password) {
        this.name = name;
        this.gender = gender;
        this.classId = classId;
        this.admissionDate = admissionDate;
        this.status = status;
        this.username = username;
        this.password = password;
        this.isAdmin = 0;
    }

    // Getter和Setter方法
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(String graduationDate) {
        this.graduationDate = graduationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
} 