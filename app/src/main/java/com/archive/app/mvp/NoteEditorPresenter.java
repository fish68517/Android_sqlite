package com.archive.app.mvp;

import android.content.Context;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Category;
import com.archive.app.model.Note;

import java.util.List;

public class NoteEditorPresenter implements NoteEditorContract.Presenter {

    private NoteEditorContract.View view;
    private OpenHelperDataBase dbHelper;

    public NoteEditorPresenter(Context context) {
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @Override
    public void attachView(NoteEditorContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadNote(long noteId) {
        if (view != null) {
            Note note = dbHelper.getNote(noteId);
            if (note != null) {
                view.showNoteDetails(note);
            }
        }
    }

    @Override
    public void loadCategories(long userId) {
        if (view != null) {
            List<Category> categories = dbHelper.getAllCategories(userId);
            view.showCategories(categories);
        }
    }

    @Override
    public void saveNote(String title, String content, long categoryId, byte[] image) {
        if (view != null) {
            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);
            note.setCategoryId(categoryId);
            note.setImage(image);
            note.setUserId(view.getUserId());
            long id = dbHelper.addNote(note);
            if (id != -1) {
                view.onNoteSaved();
            } else {
                view.showError("保存笔记失败");
            }
        }
    }

    @Override
    public void updateNote(long noteId, String title, String content, long categoryId, byte[] image) {
        if (view != null) {
            Note note = new Note();
            note.setId(noteId);
            note.setTitle(title);
            note.setContent(content);
            note.setCategoryId(categoryId);
            note.setImage(image);
            note.setUserId(view.getUserId());
            int rows = dbHelper.updateNote(note);
            if (rows > 0) {
                view.onNoteSaved();
            } else {
                view.showError("更新笔记失败");
            }
        }
    }
} 