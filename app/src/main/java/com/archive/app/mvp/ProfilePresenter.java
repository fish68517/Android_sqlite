package com.archive.app.mvp;

import android.content.Context;
import android.content.SharedPreferences;

import com.archive.app.MyApplication;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.User;

public class ProfilePresenter implements ProfileContract.Presenter {

    private ProfileContract.View view;
    private OpenHelperDataBase dbHelper;
    private Context context;

    public ProfilePresenter(Context context) {
        this.context = context;
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @Override
    public void attachView(ProfileContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadProfileData(long userId) {
        if (view != null) {
            // Load user info
            User currentUser = MyApplication.curUser; // Assume it's loaded on login
            if (currentUser != null) {
                view.showProfileInfo(currentUser);
            }

            // Load statistics
            int noteCount = dbHelper.getNotesCount(userId);
            int categoryCount = dbHelper.getCategoriesCount(userId);
            view.showStatistics(noteCount, categoryCount);
        }
    }

    @Override
    public void logout() {
        // Clear login status from SharedPreferences
        MyApplication.curUser = null;
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("user_id");
        editor.remove("username");
        editor.apply();

        if (view != null) {
            view.navigateToLogin();
        }
    }
} 