package com.example.orderfood.model;

import java.io.Serializable;

public class MerchantBean implements Serializable {
    private int merchantId;
    private String name;
    private String imageName;  // 对应mipmap资源名称
    private double rating; // 评分
    private int sales;  // -- 销售量
    private double minPrice; // -- 起送价格
    private String discountInfo; // 优惠信息
    private boolean delivery;     // 是否外送
    private String remark; // 备注
    private String category; // 分类
    private String windowLocation;
    private String businessHours;

    private String password;

    private String content; // 商家介绍

    public String getContent() {
        return content;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getters and Setters
    // ... 省略标准的getter和setter方法


    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public String getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(String discountInfo) {
        this.discountInfo = discountInfo;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWindowLocation() {
        return windowLocation;
    }

    public void setWindowLocation(String windowLocation) {
        this.windowLocation = windowLocation;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }
}