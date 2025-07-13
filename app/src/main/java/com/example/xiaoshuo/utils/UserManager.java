package com.example.xiaoshuo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.xiaoshuo.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理类，负责用户登录状态、用户数据等管理
 */
public class UserManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String USER_DATA_PREF_PREFIX = "user_data_";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_IS_PREMIUM = "is_premium";
    private static final String KEY_COINS = "coins";
    private static final String KEY_READ_TIME = "read_time";
    private static final String KEY_GENDER = "gender"; // 添加性别键
    private static final String KEY_USERS_MAP = "users_map"; // 存储所有用户凭证的映射
    
    private static UserManager instance;
    private SharedPreferences sharedPreferences;
    private User currentUser;
    private Context context;
    private Map<String, UserCredential> usersMap; // 用户名到用户凭证的映射
    private Gson gson;
    
    private UserManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadUsersMap();
        loadUserFromPrefs();
    }
    
    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }
    
    /**
     * 加载用户映射表
     */
    private void loadUsersMap() {
        String json = sharedPreferences.getString(KEY_USERS_MAP, "");
        if (json.isEmpty()) {
            usersMap = new HashMap<>();
        } else {
            Type type = new TypeToken<Map<String, UserCredential>>(){}.getType();
            try {
                usersMap = gson.fromJson(json, type);
                if (usersMap == null) {
                    usersMap = new HashMap<>();
                }
            } catch (Exception e) {
                usersMap = new HashMap<>();
            }
        }
    }
    
    /**
     * 保存用户映射表
     */
    private void saveUsersMap() {
        String json = gson.toJson(usersMap);
        sharedPreferences.edit().putString(KEY_USERS_MAP, json).apply();
    }
    
    /**
     * 从SharedPreferences加载当前登录用户数据
     */
    private void loadUserFromPrefs() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        
        if (isLoggedIn) {
            String username = sharedPreferences.getString(KEY_USERNAME, "");
            if (!username.isEmpty()) {
                loadUser(username);
            } else {
                currentUser = null;
                // 用户名为空，清除登录状态
                sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
            }
        } else {
            currentUser = null;
        }
    }
    
    /**
     * 检查用户是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean checkUserExists(String username) {
        return usersMap.containsKey(username);
    }
    
    /**
     * 验证用户密码
     * @param username 用户名
     * @param password 密码
     * @return 密码是否正确
     */
    public boolean verifyPassword(String username, String password) {
        UserCredential credential = usersMap.get(username);
        return credential != null && credential.password.equals(password);
    }
    
    /**
     * 注册新用户
     * @param username 用户名
     * @param password 密码
     * @param gender 性别
     * @return 是否注册成功
     */
    public boolean registerUser(String username, String password, String gender) {
        if (checkUserExists(username)) {
            return false;
        }
        
        // 创建用户凭证并保存
        UserCredential credential = new UserCredential(username, password, gender);
        usersMap.put(username, credential);
        saveUsersMap();
        
        // 创建用户对象
        User user = new User();
        user.setId(generateUserId());
        user.setUsername(username);
        user.setGender(gender);
        
        // 保存用户数据
        saveUserToPrefs(user);
        
        return true;
    }
    
    /**
     * 生成用户ID
     * @return 用户ID
     */
    private int generateUserId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
    
    /**
     * 加载用户
     * @param username 用户名
     * @return 是否加载成功
     */
    public boolean loadUser(String username) {
        if (!checkUserExists(username)) {
            return false;
        }
        
        // 从用户特定的SharedPreferences加载用户数据
        SharedPreferences userPrefs = context.getSharedPreferences(
                USER_DATA_PREF_PREFIX + username, Context.MODE_PRIVATE);
        
        UserCredential credential = usersMap.get(username);
        
        currentUser = new User();
        currentUser.setId(userPrefs.getInt(KEY_USER_ID, 0));
        currentUser.setUsername(username);
        currentUser.setPhoneNumber(userPrefs.getString(KEY_PHONE, ""));
        currentUser.setAvatarUrl(userPrefs.getString(KEY_AVATAR_URL, ""));
        currentUser.setPremium(userPrefs.getBoolean(KEY_IS_PREMIUM, false));
        currentUser.setCoins(userPrefs.getInt(KEY_COINS, 0));
        currentUser.setReadTime(userPrefs.getInt(KEY_READ_TIME, 0));
        currentUser.setGender(userPrefs.getString(KEY_GENDER, credential.gender));
        
        // 更新登录状态
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USERNAME, username)
                .apply();
        
        return true;
    }
    
    /**
     * 保存用户数据到用户特定的SharedPreferences
     */
    private void saveUserToPrefs(User user) {
        if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
            // 保存当前登录状态
            sharedPreferences.edit()
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .putString(KEY_USERNAME, user.getUsername())
                    .apply();
            
            // 保存用户详细数据到用户特定的SharedPreferences
            SharedPreferences userPrefs = context.getSharedPreferences(
                    USER_DATA_PREF_PREFIX + user.getUsername(), Context.MODE_PRIVATE);
            
            userPrefs.edit()
                    .putInt(KEY_USER_ID, user.getId())
                    .putString(KEY_USERNAME, user.getUsername())
                    .putString(KEY_PHONE, user.getPhoneNumber() != null ? user.getPhoneNumber() : "")
                    .putString(KEY_AVATAR_URL, user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                    .putBoolean(KEY_IS_PREMIUM, user.isPremium())
                    .putInt(KEY_COINS, user.getCoins())
                    .putInt(KEY_READ_TIME, user.getReadTime())
                    .putString(KEY_GENDER, user.getGender() != null ? user.getGender() : "male")
                    .apply();
        }
    }
    
    /**
     * 用户登录
     * @param user 用户对象
     */
    public void login(User user) {
        this.currentUser = user;
        saveUserToPrefs(user);
    }
    
    /**
     * 用户登出
     */
    public void logout() {
        this.currentUser = null;
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_USERNAME)
                .apply();
    }
    
    /**
     * 检查用户是否已登录
     * @return 是否已登录
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * 获取当前用户
     * @return 当前用户对象，未登录时返回null
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 更新用户信息
     * @param user 更新后的用户对象
     */
    public void updateUser(User user) {
        this.currentUser = user;
        saveUserToPrefs(user);
    }
    
    /**
     * 更新用户阅读时间
     * @param addedMinutes 增加的阅读分钟数
     */
    public void addReadTime(int addedMinutes) {
        if (currentUser != null) {
            currentUser.setReadTime(currentUser.getReadTime() + addedMinutes);
            saveUserToPrefs(currentUser);
        }
    }
    
    /**
     * 用户凭证类，保存用户名、密码和性别
     */
    private static class UserCredential {
        public String username;
        public String password;
        public String gender;
        
        public UserCredential() {
        }
        
        public UserCredential(String username, String password, String gender) {
            this.username = username;
            this.password = password;
            this.gender = gender;
        }
    }
} 