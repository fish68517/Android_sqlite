package com.archive.app.mvp;

import com.archive.app.model.User;

public interface ProfileContract {

    interface View {
        void showProfileInfo(User user);
        void showStatistics(int noteCount, int categoryCount);
        void navigateToLogin();
        long getUserId();
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadProfileData(long userId);
        void logout();
    }
} 