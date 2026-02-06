package com.example.healthdietapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.healthdietapp.models.UserRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UserRecipeDAO - Data Access Object for UserRecipe operations
 * Handles user recipe schedule CRUD operations and conflict detection
 */
public class UserRecipeDAO {
    private DatabaseHelper dbHelper;

    public UserRecipeDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Add a recipe to user's schedule
     */
    public boolean addUserRecipe(UserRecipe userRecipe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (userRecipe.getUserRecipeId() == null) {
                userRecipe.setUserRecipeId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("user_recipe_id", userRecipe.getUserRecipeId());
            values.put("user_id", userRecipe.getUserId());
            values.put("recipe_id", userRecipe.getRecipeId());
            values.put("date", userRecipe.getDate());
            values.put("meal_type", userRecipe.getMealType());
            values.put("added_at", userRecipe.getAddedAt());
            
            long result = db.insert("user_recipes", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get user recipe by ID
     */
    public UserRecipe getUserRecipeById(String userRecipeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("user_recipes", null, "user_recipe_id = ?", 
                    new String[]{userRecipeId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                UserRecipe userRecipe = cursorToUserRecipe(cursor);
                cursor.close();
                return userRecipe;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get user recipes for a specific date
     */
    public List<UserRecipe> getUserRecipesByDate(String userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<UserRecipe> userRecipes = new ArrayList<>();
        try {
            Cursor cursor = db.query("user_recipes", null, 
                    "user_id = ? AND date = ?", 
                    new String[]{userId, date}, null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    userRecipes.add(cursorToUserRecipe(cursor));
                }
                cursor.close();
            }
            return userRecipes;
        } finally {
            db.close();
        }
    }

    /**
     * Get user recipe for a specific meal
     */
    public UserRecipe getUserRecipeByMeal(String userId, String date, String mealType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("user_recipes", null, 
                    "user_id = ? AND date = ? AND meal_type = ?", 
                    new String[]{userId, date, mealType}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                UserRecipe userRecipe = cursorToUserRecipe(cursor);
                cursor.close();
                return userRecipe;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get all user recipes for a date range
     */
    public List<UserRecipe> getUserRecipesByDateRange(String userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<UserRecipe> userRecipes = new ArrayList<>();
        try {
            Cursor cursor = db.query("user_recipes", null, 
                    "user_id = ? AND date >= ? AND date <= ?", 
                    new String[]{userId, startDate, endDate}, null, null, "date ASC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    userRecipes.add(cursorToUserRecipe(cursor));
                }
                cursor.close();
            }
            return userRecipes;
        } finally {
            db.close();
        }
    }

    /**
     * Check if there's a conflict for a specific meal
     */
    public boolean hasConflict(String userId, String date, String mealType) {
        return getUserRecipeByMeal(userId, date, mealType) != null;
    }

    /**
     * Replace existing recipe for a meal
     */
    public boolean replaceUserRecipe(String userId, String date, String mealType, String newRecipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("recipe_id", newRecipeId);
            values.put("added_at", System.currentTimeMillis());
            
            int result = db.update("user_recipes", values, 
                    "user_id = ? AND date = ? AND meal_type = ?", 
                    new String[]{userId, date, mealType});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Add extra meal (for handling conflicts with "add meal" option)
     * This creates a new meal entry with a modified meal type (e.g., "breakfast_extra")
     */
    public boolean addExtraMeal(String userId, String date, String mealType, String recipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            UserRecipe userRecipe = new UserRecipe();
            userRecipe.setUserRecipeId(UUID.randomUUID().toString());
            userRecipe.setUserId(userId);
            userRecipe.setRecipeId(recipeId);
            userRecipe.setDate(date);
            userRecipe.setMealType(mealType + "_extra");
            userRecipe.setAddedAt(System.currentTimeMillis());
            
            ContentValues values = new ContentValues();
            values.put("user_recipe_id", userRecipe.getUserRecipeId());
            values.put("user_id", userRecipe.getUserId());
            values.put("recipe_id", userRecipe.getRecipeId());
            values.put("date", userRecipe.getDate());
            values.put("meal_type", userRecipe.getMealType());
            values.put("added_at", userRecipe.getAddedAt());
            
            long result = db.insert("user_recipes", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Update user recipe
     */
    public boolean updateUserRecipe(UserRecipe userRecipe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("recipe_id", userRecipe.getRecipeId());
            values.put("date", userRecipe.getDate());
            values.put("meal_type", userRecipe.getMealType());
            values.put("added_at", userRecipe.getAddedAt());
            
            int result = db.update("user_recipes", values, "user_recipe_id = ?", 
                    new String[]{userRecipe.getUserRecipeId()});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete user recipe
     */
    public boolean deleteUserRecipe(String userRecipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("user_recipes", "user_recipe_id = ?", 
                    new String[]{userRecipeId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete user recipe by meal
     */
    public boolean deleteUserRecipeByMeal(String userId, String date, String mealType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("user_recipes", 
                    "user_id = ? AND date = ? AND meal_type = ?", 
                    new String[]{userId, date, mealType});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete all user recipes
     */
    public boolean deleteUserRecipes(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("user_recipes", "user_id = ?", 
                    new String[]{userId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Convert cursor to UserRecipe object
     */
    private UserRecipe cursorToUserRecipe(Cursor cursor) {
        UserRecipe userRecipe = new UserRecipe();
        userRecipe.setUserRecipeId(cursor.getString(cursor.getColumnIndexOrThrow("user_recipe_id")));
        userRecipe.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
        userRecipe.setRecipeId(cursor.getString(cursor.getColumnIndexOrThrow("recipe_id")));
        userRecipe.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
        userRecipe.setMealType(cursor.getString(cursor.getColumnIndexOrThrow("meal_type")));
        userRecipe.setAddedAt(cursor.getLong(cursor.getColumnIndexOrThrow("added_at")));
        return userRecipe;
    }
}
