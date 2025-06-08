package com.archive.app.mvp;

import android.content.Context;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Note;

import java.util.List;

public class NotePresenter implements NoteContract.Presenter {

    private NoteContract.View view;
    private OpenHelperDataBase dbHelper;

    public NotePresenter(Context context) {
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @Override
    public void attachView(NoteContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadNotes(long userId) {
        if (view != null) {
            view.showLoading();
            List<Note> notes = dbHelper.getAllNotes(userId);
            view.hideLoading();
            if (notes.isEmpty()) {
                view.showEmptyView();
            } else {
                view.showNotes(notes);
            }
        }
    }

    @Override
    public void loadNotesByCategory(long userId, long categoryId) {
        if (view != null) {
            view.showLoading();
            List<Note> notes = dbHelper.getNotesByCategory(userId, categoryId);
            view.hideLoading();
            if (notes.isEmpty()) {
                view.showEmptyView();
            } else {
                view.showNotes(notes);
            }
        }
    }

    @Override
    public void deleteNote(long noteId) {
        dbHelper.deleteNote(noteId);
        if (view != null) {
            loadNotes(view.getUserId());
        }
    }
} 