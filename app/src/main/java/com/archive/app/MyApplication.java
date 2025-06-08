package com.archive.app;

import com.archive.app.model.User;

public class MyApplication extends android.app.Application{


    public static User curUser;

    public static void setUser(User user) {

        // save user to shared preferences or database or any other storage
        curUser = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
