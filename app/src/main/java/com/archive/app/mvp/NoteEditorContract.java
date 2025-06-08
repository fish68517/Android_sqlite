package com.archive.app.mvp;

import com.archive.app.model.Category;
import com.archive.app.model.Note;
import java.util.List;

public interface NoteEditorContract {

    interface View {
        void showNoteDetails(Note note);
        void showCategories(List<Category> categories);
        void onNoteSaved();
        void showError(String message);
        long getUserId();
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadNote(long noteId);
        void loadCategories(long userId);
        void saveNote(String title, String content, long categoryId, byte[] image);
        void updateNote(long noteId, String title, String content, long categoryId, byte[] image);
    }
} 