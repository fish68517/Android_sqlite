package com.example.orderfood;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.example.orderfood.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;
    public static User user;

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    // 保存用户登陆信息
    public static void saveUser(User usertemp) {
        // 实现本地持久化存储
        user = usertemp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
} 