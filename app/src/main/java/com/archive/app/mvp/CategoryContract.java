package com.archive.app.mvp;

import com.archive.app.model.Category;
import java.util.List;

public interface CategoryContract {

    interface View {
        void showCategories(List<Category> categories);
        void showEmptyView();
        long getUserId();
        void onCategoryAdded();
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadCategories(long userId);
        void addCategory(String name, long userId);
        void deleteCategory(long categoryId);
    }
} 