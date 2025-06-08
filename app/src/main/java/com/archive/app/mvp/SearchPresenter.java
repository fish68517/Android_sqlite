package com.archive.app.mvp;

import android.content.Context;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Note;
import java.util.List;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View view;
    private OpenHelperDataBase dbHelper;

    public SearchPresenter(Context context) {
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @Override
    public void attachView(SearchContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void searchNotes(long userId, String keyword) {
        if (view != null) {
            view.showLoading();
            List<Note> notes = dbHelper.searchNotes(userId, keyword);
            view.hideLoading();
            if (notes.isEmpty()) {
                view.showEmptyResults();
            } else {
                view.showSearchResults(notes);
            }
        }
    }
} 