package com.myapplication.app;

import android.app.Application;

import com.myapplication.app.model.User;

public class MyApplication extends Application{


    private static User user;

    public static void setUser(User user) {
        MyApplication.user = user;
    }

    public static User getUser() {
        return user;
    }
}
