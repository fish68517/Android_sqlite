package com.example.orderfood.model;


import java.io.Serializable;

/**
 * 菜品表 Dishes 对应的 JavaBean
 */
public class Dish implements Serializable {
    private int dishId;
    private String name;
    private double price;
    private String imageUrl;
    private String category;
    private int merchantId;

    private String merchantName;

    private String description;
    private int browseCount;

    private int sales;
    private int stock;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    private String specifications;

    public Dish() {


    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    public Dish(String name, double price, String urlToImage4) {
        this.name = name;
        this.price = price;
        this.imageUrl = urlToImage4;
    }

    // Getters and Setters
    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        // 截断.jpg
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }


    @Override
    public String toString() {
        return "Dish{" +
                "dishId=" + dishId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                ", merchantId=" + merchantId +
                ", description='" + description + '\'' +
                ", browseCount=" + browseCount +
                '}';
    }
}
