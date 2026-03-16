package com.hakimi;

import android.app.Application;

import com.hakimi.local.LocalHealthRepository;
import com.hakimi.model.User;

/**
 * 应用程序主类
 * 
 * @author hakimi
 */
public class HakimiApplication extends Application {

    private static HakimiApplication instance;
    public static User curUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LocalHealthRepository.getInstance(this).ensureSeedData();
    }

    public static HakimiApplication getInstance() {
        return instance;
    }

    public static void setUser(User user) {
        curUser = user;
    }
}

