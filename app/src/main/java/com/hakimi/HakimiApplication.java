package com.hakimi;

import android.app.Application;

import com.hakimi.model.User;

/**
 * 应用程序主类
 * 
 * @author hakimi
 */
public class HakimiApplication extends Application {

    private static HakimiApplication instance;
    public static User curUser = new User(9);

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static HakimiApplication getInstance() {
        return instance;
    }

    public static void setUser(User user) {
        curUser = user;
    }
}

