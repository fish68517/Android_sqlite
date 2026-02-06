package com.example.healthdietapp.models;

/**
 * UserRecipe Model - Represents a user's scheduled recipe for a meal
 */
public class UserRecipe {
    private String userRecipeId;
    private String userId;
    private String recipeId;
    private String date;
    private String mealType;
    private long addedAt;

    public UserRecipe() {
    }

    public UserRecipe(String userRecipeId, String userId, String recipeId, String date, String mealType) {
        this.userRecipeId = userRecipeId;
        this.userId = userId;
        this.recipeId = recipeId;
        this.date = date;
        this.mealType = mealType;
        this.addedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserRecipeId() {
        return userRecipeId;
    }

    public void setUserRecipeId(String userRecipeId) {
        this.userRecipeId = userRecipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }
}
