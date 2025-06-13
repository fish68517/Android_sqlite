package com.example.studentmanager.model;

/**
 * 班级数据模型类
 */
public class Class {
    private int classId;
    private String className;
    private String classType;
    private String createTime;

    // 构造函数
    public Class() {}

    public Class(String className, String classType) {
        this.className = className;
        this.classType = classType;
    }

    // Getter和Setter方法
    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
} 