package com.example.orderfood;

import android.app.Application;
import android.content.Context;

import com.example.orderfood.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {

    public static Context mContext;
    public static User user;


    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }





    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;


    }






    // 保存用户登陆信息
    public static void saveUser(User usertemp) {
        // 实现本地持久化存储
        user =  usertemp;
    }




}
