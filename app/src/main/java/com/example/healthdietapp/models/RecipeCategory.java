package com.example.healthdietapp.models;

/**
 * RecipeCategory Model - Represents recipe categories and subcategories
 */
public class RecipeCategory {
    private String categoryId;
    private String name;
    private String parentCategoryId;
    private String icon;

    public RecipeCategory() {
    }

    public RecipeCategory(String categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // Getters and Setters
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
