package com.example.orderfood.model;

public class DishCategory {


    private int categoryId;

    public DishCategory() {

    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    private String name;
    private String imageUrl;

    public DishCategory(String categoryTitle, String categoryImage) {
        this.name = categoryTitle;
        this.imageUrl = categoryImage;
    }

    public String getCategoryTitle() {
        return name;
    }


    public String getCategoryImage() {
        return imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
