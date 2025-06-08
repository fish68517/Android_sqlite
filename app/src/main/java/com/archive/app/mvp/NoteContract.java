package com.archive.app.mvp;

import com.archive.app.model.Note;
import java.util.List;

public interface NoteContract {

    interface View {
        void showNotes(List<Note> notes);
        void showEmptyView();
        void showLoading();
        void hideLoading();
        long getUserId(); // To get current user id
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadNotes(long userId);
        void loadNotesByCategory(long userId, long categoryId);
        void deleteNote(long noteId);
    }
} 