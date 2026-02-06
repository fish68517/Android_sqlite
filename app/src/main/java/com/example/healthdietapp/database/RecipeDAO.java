package com.example.healthdietapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.healthdietapp.models.Recipe;
import com.example.healthdietapp.models.RecipeCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RecipeDAO - Data Access Object for Recipe operations
 * Handles recipe CRUD operations, category queries, and recommendation queries
 */
public class RecipeDAO {
    private DatabaseHelper dbHelper;

    public RecipeDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Create a new recipe
     */
    public boolean createRecipe(Recipe recipe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (recipe.getRecipeId() == null) {
                recipe.setRecipeId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("recipe_id", recipe.getRecipeId());
            values.put("name", recipe.getName());
            values.put("description", recipe.getDescription());
            values.put("ingredients", recipe.getIngredients());
            values.put("instructions", recipe.getInstructions());
            values.put("nutrition_info", recipe.getNutritionInfo());
            values.put("category", recipe.getCategory());
            values.put("image_url", recipe.getImageUrl());
            values.put("created_by", recipe.getCreatedBy());
            values.put("created_at", recipe.getCreatedAt());
            values.put("updated_at", recipe.getUpdatedAt());
            
            long result = db.insert("recipes", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get recipe by ID
     */
    public Recipe getRecipeById(String recipeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("recipes", null, "recipe_id = ?", 
                    new String[]{recipeId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                Recipe recipe = cursorToRecipe(cursor);
                cursor.close();
                return recipe;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get all recipes
     */
    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        try {
            Cursor cursor = db.query("recipes", null, null, null, null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    recipes.add(cursorToRecipe(cursor));
                }
                cursor.close();
            }
            return recipes;
        } finally {
            db.close();
        }
    }

    /**
     * Get recipes by category
     */
    public List<Recipe> getRecipesByCategory(String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        try {
            Cursor cursor = db.query("recipes", null, "category = ?", 
                    new String[]{category}, null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    recipes.add(cursorToRecipe(cursor));
                }
                cursor.close();
            }
            return recipes;
        } finally {
            db.close();
        }
    }

    /**
     * Search recipes by name or description
     */
    public List<Recipe> searchRecipes(String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        try {
            String searchPattern = "%" + keyword + "%";
            Cursor cursor = db.query("recipes", null, 
                    "name LIKE ? OR description LIKE ?", 
                    new String[]{searchPattern, searchPattern}, null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    recipes.add(cursorToRecipe(cursor));
                }
                cursor.close();
            }
            return recipes;
        } finally {
            db.close();
        }
    }

    /**
     * Get recommended recipes (limit to 10)
     */
    public List<Recipe> getRecommendedRecipes(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        try {
            Cursor cursor = db.query("recipes", null, null, null, null, null, 
                    "created_at DESC LIMIT " + limit);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    recipes.add(cursorToRecipe(cursor));
                }
                cursor.close();
            }
            return recipes;
        } finally {
            db.close();
        }
    }

    /**
     * Update recipe
     */
    public boolean updateRecipe(Recipe recipe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            recipe.setUpdatedAt(System.currentTimeMillis());
            
            ContentValues values = new ContentValues();
            values.put("name", recipe.getName());
            values.put("description", recipe.getDescription());
            values.put("ingredients", recipe.getIngredients());
            values.put("instructions", recipe.getInstructions());
            values.put("nutrition_info", recipe.getNutritionInfo());
            values.put("category", recipe.getCategory());
            values.put("image_url", recipe.getImageUrl());
            values.put("updated_at", recipe.getUpdatedAt());
            
            int result = db.update("recipes", values, "recipe_id = ?", 
                    new String[]{recipe.getRecipeId()});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete recipe
     */
    public boolean deleteRecipe(String recipeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("recipes", "recipe_id = ?", 
                    new String[]{recipeId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Create recipe category
     */
    public boolean createCategory(RecipeCategory category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (category.getCategoryId() == null) {
                category.setCategoryId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("category_id", category.getCategoryId());
            values.put("name", category.getName());
            values.put("parent_category_id", category.getParentCategoryId());
            values.put("icon", category.getIcon());
            
            long result = db.insert("recipe_categories", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get all main categories (parent_category_id is null)
     */
    public List<RecipeCategory> getMainCategories() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<RecipeCategory> categories = new ArrayList<>();
        try {
            Cursor cursor = db.query("recipe_categories", null, 
                    "parent_category_id IS NULL", null, null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    categories.add(cursorToRecipeCategory(cursor));
                }
                cursor.close();
            }
            return categories;
        } finally {
            db.close();
        }
    }

    /**
     * Get subcategories by parent category ID
     */
    public List<RecipeCategory> getSubcategories(String parentCategoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<RecipeCategory> categories = new ArrayList<>();
        try {
            Cursor cursor = db.query("recipe_categories", null, 
                    "parent_category_id = ?", new String[]{parentCategoryId}, 
                    null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    categories.add(cursorToRecipeCategory(cursor));
                }
                cursor.close();
            }
            return categories;
        } finally {
            db.close();
        }
    }

    /**
     * Get category by ID
     */
    public RecipeCategory getCategoryById(String categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("recipe_categories", null, "category_id = ?", 
                    new String[]{categoryId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                RecipeCategory category = cursorToRecipeCategory(cursor);
                cursor.close();
                return category;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Convert cursor to Recipe object
     */
    private Recipe cursorToRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(cursor.getString(cursor.getColumnIndexOrThrow("recipe_id")));
        recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        recipe.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        recipe.setIngredients(cursor.getString(cursor.getColumnIndexOrThrow("ingredients")));
        recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow("instructions")));
        recipe.setNutritionInfo(cursor.getString(cursor.getColumnIndexOrThrow("nutrition_info")));
        recipe.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
        recipe.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
        recipe.setCreatedBy(cursor.getString(cursor.getColumnIndexOrThrow("created_by")));
        recipe.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
        recipe.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updated_at")));
        return recipe;
    }

    /**
     * Get user's collected recipes (from user_recipes table)
     */
    public List<Recipe> getUserCollectedRecipes(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT DISTINCT r.* FROM recipes r " +
                    "INNER JOIN user_recipes ur ON r.recipe_id = ur.recipe_id " +
                    "WHERE ur.user_id = ? " +
                    "ORDER BY ur.added_at DESC", 
                    new String[]{userId});
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    recipes.add(cursorToRecipe(cursor));
                }
                cursor.close();
            }
            return recipes;
        } finally {
            db.close();
        }
    }

    /**
     * Convert cursor to RecipeCategory object
     */
    private RecipeCategory cursorToRecipeCategory(Cursor cursor) {
        RecipeCategory category = new RecipeCategory();
        category.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("category_id")));
        category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        category.setParentCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("parent_category_id")));
        category.setIcon(cursor.getString(cursor.getColumnIndexOrThrow("icon")));
        return category;
    }
}
