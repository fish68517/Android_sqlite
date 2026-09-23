package com.personal.diary;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "diary_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveLogin(long userId, String username) {
        saveLogin(userId, username, "user");
    }

    public void saveLogin(long userId, String username, String role) {
        preferences.edit()
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public boolean isLoggedIn() {
        return getUserId() > 0;
    }

    public long getUserId() {
        return preferences.getLong(KEY_USER_ID, -1L);
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    public String getRole() {
        return preferences.getString(KEY_ROLE, "user");
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    public void logout() {
        preferences.edit().clear().apply();
    }
}
