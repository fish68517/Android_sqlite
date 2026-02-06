package com.example.healthdietapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager - Manages user login session and preferences
 */
public class SessionManager {

    private static final String PREF_NAME = "HealthDietAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGIN_TOKEN = "loginToken";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveLoginSession(String userId, String username, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_LOGIN_TOKEN, token);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getLoginToken() {
        return sharedPreferences.getString(KEY_LOGIN_TOKEN, null);
    }

    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_USER_ID, null);
        editor.putString(KEY_USERNAME, null);
        editor.putString(KEY_LOGIN_TOKEN, null);
        editor.apply();
    }
}
