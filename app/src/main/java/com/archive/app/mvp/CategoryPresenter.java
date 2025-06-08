package com.archive.app.mvp;

import android.content.Context;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Category;
import java.util.List;

public class CategoryPresenter implements CategoryContract.Presenter {

    private CategoryContract.View view;
    private OpenHelperDataBase dbHelper;

    public CategoryPresenter(Context context) {
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @Override
    public void attachView(CategoryContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadCategories(long userId) {
        if (view != null) {
            List<Category> categories = dbHelper.getAllCategories(userId);
            if (categories.isEmpty()) {
                view.showEmptyView();
            } else {
                view.showCategories(categories);
            }
        }
    }

    @Override
    public void addCategory(String name, long userId) {
        dbHelper.addCategory(name, userId);
        if (view != null) {
            view.onCategoryAdded();
            loadCategories(userId);
        }
    }

    @Override
    public void deleteCategory(long categoryId) {
        dbHelper.deleteCategory(categoryId);
        if (view != null) {
            loadCategories(view.getUserId());
        }
    }
} 