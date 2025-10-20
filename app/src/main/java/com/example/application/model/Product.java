package com.example.application.model;// =================================================================================
// 文件路径: app/src/main/java/com/example/stylehub/data/model/Product.java
// 任务: 离线模式 (Offline Mode with Room) - 级别 3
// 描述: 这是Product数据模型。@Entity注解表明它是一个Room数据库的表。
// =================================================================================

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "products")
public class Product implements Serializable { // Serializable for passing via Intent
    @PrimaryKey
    private final int id;
    private final String name;
    private final String description;
    private final double price;
    private final String imageUrl;

    public Product(int id, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}