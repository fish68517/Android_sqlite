package com.hakimi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hakimi.HakimiApplication;

/**
 * SharedPreferences管理类
 * 
 * @author hakimi
 */
public class SharedPrefManager {

    private static final String PREF_NAME = "hakimi_pref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_LOGIN_ACCOUNT = "login_account";
    private static final String KEY_LOGIN_PASSWORD = "login_password";
    private static final String KEY_REMEMBER_LOGIN = "remember_login";

    private static SharedPrefManager instance;
    private SharedPreferences preferences;

    private SharedPrefManager() {
        preferences = HakimiApplication.getInstance()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefManager getInstance() {
        if (instance == null) {
            synchronized (SharedPrefManager.class) {
                if (instance == null) {
                    instance = new SharedPrefManager();
                }
            }
        }
        return instance;
    }

    /**
     * 保存Token
     */
    public void saveToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    /**
     * 获取Token
     */
    public String getToken() {
        return preferences.getString(KEY_TOKEN, "");
    }

    /**
     * 保存用户ID
     */
    public void saveUserId(Long userId) {
        preferences.edit().putLong(KEY_USER_ID, userId).apply();
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return preferences.getLong(KEY_USER_ID, 0);
    }

    /**
     * 保存用户名
     */
    public void saveUsername(String username) {
        preferences.edit().putString(KEY_USERNAME, username).apply();
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    /**
     * 保存邮箱
     */
    public void savePhone(String phone) {
        preferences.edit().putString(KEY_PHONE, phone).apply();
    }

    /**
     * 获取邮箱
     */
    public String getPhone() {
        return preferences.getString(KEY_PHONE, "");
    }

    public void saveLoginAccount(String account) {
        preferences.edit().putString(KEY_LOGIN_ACCOUNT, account).apply();
    }

    public String getLoginAccount() {
        return preferences.getString(KEY_LOGIN_ACCOUNT, "");
    }

    public void saveLoginPassword(String password) {
        preferences.edit().putString(KEY_LOGIN_PASSWORD, password).apply();
    }

    public String getLoginPassword() {
        return preferences.getString(KEY_LOGIN_PASSWORD, "");
    }

    public void saveRememberLogin(boolean rememberLogin) {
        preferences.edit().putBoolean(KEY_REMEMBER_LOGIN, rememberLogin).apply();
    }

    public boolean isRememberLogin() {
        return preferences.getBoolean(KEY_REMEMBER_LOGIN, false);
    }

    public void clearSavedLogin() {
        preferences.edit()
                .remove(KEY_LOGIN_ACCOUNT)
                .remove(KEY_LOGIN_PASSWORD)
                .putBoolean(KEY_REMEMBER_LOGIN, false)
                .apply();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        preferences.edit().clear().apply();
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return !com.hakimi.utils.TextUtils.isEmpty(getToken());
    }
}

