package com.example.application.model;// =================================================================================
// 文件路径: app/src/main/java/com/example/geeknotes/data/model/Note.java
// 任务: MVVM 架构 - 级别 3
// 描述: 这是Note数据模型，@Entity注解表明它是一个Room数据库的表。
// =================================================================================


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}