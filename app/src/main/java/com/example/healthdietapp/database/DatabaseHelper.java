package com.example.healthdietapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper - Manages SQLite database creation and upgrades
 * Handles all database table creation and schema management
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "health_diet_app.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all database tables
        createUsersTables(db);
        createRecipeTables(db);
        createCommunityTables(db);
        createUtilityTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database version upgrades
        // For now, drop all tables and recreate them
        dropAllTables(db);
        onCreate(db);
    }

    private void createUsersTables(SQLiteDatabase db) {
        // Users table
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "user_id TEXT PRIMARY KEY," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "nickname TEXT," +
                "avatar TEXT," +
                "created_at INTEGER," +
                "updated_at INTEGER)");

        // User preferences table
        db.execSQL("CREATE TABLE IF NOT EXISTS user_preferences (" +
                "preference_id TEXT PRIMARY KEY," +
                "user_id TEXT UNIQUE NOT NULL," +
                "taste_tendency TEXT," +
                "diet_type TEXT," +
                "health_goal TEXT," +
                "restrictions TEXT," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");
    }

    private void createRecipeTables(SQLiteDatabase db) {
        // Recipes table
        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                "recipe_id TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "ingredients TEXT," +
                "instructions TEXT," +
                "nutrition_info TEXT," +
                "category TEXT," +
                "image_url TEXT," +
                "created_by TEXT," +
                "created_at INTEGER," +
                "updated_at INTEGER)");

        // User recipes table
        db.execSQL("CREATE TABLE IF NOT EXISTS user_recipes (" +
                "user_recipe_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "recipe_id TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "meal_type TEXT NOT NULL," +
                "added_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id)," +
                "UNIQUE(user_id, date, meal_type))");

        // Health records table
        db.execSQL("CREATE TABLE IF NOT EXISTS health_records (" +
                "record_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "weight REAL," +
                "water_intake REAL," +
                "measurements TEXT," +
                "recorded_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "UNIQUE(user_id, date))");

        // Recipe categories table
        db.execSQL("CREATE TABLE IF NOT EXISTS recipe_categories (" +
                "category_id TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "parent_category_id TEXT," +
                "icon TEXT," +
                "FOREIGN KEY (parent_category_id) REFERENCES recipe_categories(category_id))");
    }

    private void createCommunityTables(SQLiteDatabase db) {
        // Posts table
        db.execSQL("CREATE TABLE IF NOT EXISTS posts (" +
                "post_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "title TEXT NOT NULL," +
                "content TEXT," +
                "images TEXT," +
                "tags TEXT," +
                "likes INTEGER DEFAULT 0," +
                "comments INTEGER DEFAULT 0," +
                "created_at INTEGER," +
                "updated_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");

        // Post likes table
        db.execSQL("CREATE TABLE IF NOT EXISTS post_likes (" +
                "like_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "post_id TEXT NOT NULL," +
                "liked_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (post_id) REFERENCES posts(post_id)," +
                "UNIQUE(user_id, post_id))");

        // Post collections table
        db.execSQL("CREATE TABLE IF NOT EXISTS post_collections (" +
                "collection_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "post_id TEXT NOT NULL," +
                "collected_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (post_id) REFERENCES posts(post_id)," +
                "UNIQUE(user_id, post_id))");

        // User follows table
        db.execSQL("CREATE TABLE IF NOT EXISTS user_follows (" +
                "follow_id TEXT PRIMARY KEY," +
                "follower_id TEXT NOT NULL," +
                "followee_id TEXT NOT NULL," +
                "followed_at INTEGER," +
                "FOREIGN KEY (follower_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (followee_id) REFERENCES users(user_id)," +
                "UNIQUE(follower_id, followee_id))");

        // Health questions table
        db.execSQL("CREATE TABLE IF NOT EXISTS health_questions (" +
                "question_id TEXT PRIMARY KEY," +
                "question TEXT NOT NULL," +
                "answer TEXT," +
                "category TEXT," +
                "created_at INTEGER)");
    }

    private void createUtilityTables(SQLiteDatabase db) {
        // Search history table
        db.execSQL("CREATE TABLE IF NOT EXISTS search_history (" +
                "history_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "keyword TEXT NOT NULL," +
                "searched_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");

        // Feedbacks table
        db.execSQL("CREATE TABLE IF NOT EXISTS feedbacks (" +
                "feedback_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "created_at INTEGER," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");
    }

    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS feedbacks");
        db.execSQL("DROP TABLE IF EXISTS search_history");
        db.execSQL("DROP TABLE IF EXISTS health_questions");
        db.execSQL("DROP TABLE IF EXISTS user_follows");
        db.execSQL("DROP TABLE IF EXISTS post_collections");
        db.execSQL("DROP TABLE IF EXISTS post_likes");
        db.execSQL("DROP TABLE IF EXISTS posts");
        db.execSQL("DROP TABLE IF EXISTS recipe_categories");
        db.execSQL("DROP TABLE IF EXISTS health_records");
        db.execSQL("DROP TABLE IF EXISTS user_recipes");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS user_preferences");
        db.execSQL("DROP TABLE IF EXISTS users");
    }
}
