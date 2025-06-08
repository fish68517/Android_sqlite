package com.archive.app.mvp;

import com.archive.app.model.Note;
import java.util.List;

public interface SearchContract {

    interface View {
        void showSearchResults(List<Note> notes);
        void showEmptyResults();
        void showLoading();
        void hideLoading();
        long getUserId();
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void searchNotes(long userId, String keyword);
    }
} 