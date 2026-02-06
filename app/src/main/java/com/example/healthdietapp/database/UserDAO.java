package com.example.healthdietapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.models.UserPreferences;
import java.util.UUID;

/**
 * UserDAO - Data Access Object for User operations
 * Handles user CRUD operations, login verification, and preference management
 */
public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Create a new user
     */
    public boolean createUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (user.getUserId() == null) {
                user.setUserId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("user_id", user.getUserId());
            values.put("username", user.getUsername());
            values.put("password", user.getPassword());
            values.put("nickname", user.getNickname());
            values.put("avatar", user.getAvatar());
            values.put("created_at", user.getCreatedAt());
            values.put("updated_at", user.getUpdatedAt());
            
            long result = db.insert("users", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get user by ID
     */
    public User getUserById(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("users", null, "user_id = ?", 
                    new String[]{userId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                User user = cursorToUser(cursor);
                cursor.close();
                return user;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("users", null, "username = ?", 
                    new String[]{username}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                User user = cursorToUser(cursor);
                cursor.close();
                return user;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Verify login credentials
     * Compares encrypted password with stored hash
     */
    public User verifyLogin(String username, String encryptedPassword) {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword().equals(encryptedPassword)) {
            return user;
        }
        return null;
    }

    /**
     * Update user information
     */
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            user.setUpdatedAt(System.currentTimeMillis());
            
            ContentValues values = new ContentValues();
            values.put("nickname", user.getNickname());
            values.put("avatar", user.getAvatar());
            values.put("updated_at", user.getUpdatedAt());
            
            int result = db.update("users", values, "user_id = ?", 
                    new String[]{user.getUserId()});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Update user password
     */
    public boolean updatePassword(String userId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("password", newPassword);
            values.put("updated_at", System.currentTimeMillis());
            
            int result = db.update("users", values, "user_id = ?", 
                    new String[]{userId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete user
     */
    public boolean deleteUser(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("users", "user_id = ?", 
                    new String[]{userId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return getUserByUsername(username) != null;
    }

    /**
     * Save user preferences
     */
    public boolean saveUserPreferences(UserPreferences preferences) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (preferences.getPreferenceId() == null) {
                preferences.setPreferenceId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("preference_id", preferences.getPreferenceId());
            values.put("user_id", preferences.getUserId());
            values.put("taste_tendency", preferences.getTasteTendency());
            values.put("diet_type", preferences.getDietType());
            values.put("health_goal", preferences.getHealthGoal());
            values.put("restrictions", preferences.getRestrictions());
            
            long result = db.insertWithOnConflict("user_preferences", null, values, 
                    SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get user preferences
     */
    public UserPreferences getUserPreferences(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("user_preferences", null, "user_id = ?", 
                    new String[]{userId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                UserPreferences preferences = cursorToUserPreferences(cursor);
                cursor.close();
                return preferences;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Update user preferences
     */
    public boolean updateUserPreferences(UserPreferences preferences) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("taste_tendency", preferences.getTasteTendency());
            values.put("diet_type", preferences.getDietType());
            values.put("health_goal", preferences.getHealthGoal());
            values.put("restrictions", preferences.getRestrictions());
            
            int result = db.update("user_preferences", values, "user_id = ?", 
                    new String[]{preferences.getUserId()});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete user preferences
     */
    public boolean deleteUserPreferences(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("user_preferences", "user_id = ?", 
                    new String[]{userId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Convert cursor to User object
     */
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow("nickname")));
        user.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow("avatar")));
        user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
        user.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updated_at")));
        return user;
    }

    /**
     * Convert cursor to UserPreferences object
     */
    private UserPreferences cursorToUserPreferences(Cursor cursor) {
        UserPreferences preferences = new UserPreferences();
        preferences.setPreferenceId(cursor.getString(cursor.getColumnIndexOrThrow("preference_id")));
        preferences.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
        preferences.setTasteTendency(cursor.getString(cursor.getColumnIndexOrThrow("taste_tendency")));
        preferences.setDietType(cursor.getString(cursor.getColumnIndexOrThrow("diet_type")));
        preferences.setHealthGoal(cursor.getString(cursor.getColumnIndexOrThrow("health_goal")));
        preferences.setRestrictions(cursor.getString(cursor.getColumnIndexOrThrow("restrictions")));
        return preferences;
    }
}
