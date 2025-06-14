package com.example.orderfood.model;

import java.io.Serializable;

// 对应购物车 orderrecords
public class CartItem implements Serializable {
    private int recordId;
    private int studentId;
    private int merchantId;
    private int dishId;
    private int quantity;
    private String dishName;
    private double price;
    private String imageUrl;
    private String merchantName;

    // Getters and Setters
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        if(imageUrl.endsWith(".jpg")) {
            imageUrl = imageUrl.substring(0, imageUrl.length() - 4);
        } else if (imageUrl.endsWith(".png")) {
            imageUrl = imageUrl.substring(0, imageUrl.length() - 4);
        } else if (imageUrl.endsWith(".jpeg")) {
            imageUrl = imageUrl.substring(0, imageUrl.length() - 5);
        } else if (imageUrl.endsWith(".gif")) {
            imageUrl = imageUrl.substring(0, imageUrl.length() - 4);
        } else if (imageUrl.endsWith(".svg")) {
            imageUrl = imageUrl.substring(0, imageUrl.length() - 4);
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrlkkk) {
        // 截断.jpg
        if(imageUrlkkk.endsWith(".jpg")) {
            imageUrlkkk = imageUrlkkk.substring(0, imageUrlkkk.length() - 4);
        } else if (imageUrlkkk.endsWith(".png")) {
            imageUrlkkk = imageUrlkkk.substring(0, imageUrlkkk.length() - 4);
        } else if (imageUrlkkk.endsWith(".jpeg")) {
            imageUrlkkk = imageUrlkkk.substring(0, imageUrlkkk.length() - 5);
        } else if (imageUrlkkk.endsWith(".gif")) {
            imageUrlkkk = imageUrlkkk.substring(0, imageUrlkkk.length() - 4);
        } else if (imageUrlkkk.endsWith(".svg")) {
            imageUrlkkk = imageUrlkkk.substring(0, imageUrlkkk.length() - 4);
        }
        this.imageUrl = imageUrlkkk;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    // 计算小计金额
    public double getSubtotal() {
        return price * quantity;
    }
} 