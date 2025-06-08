package com.archive.app.mvp;

import android.content.Context;
import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.Note;
import java.util.List;

public class RecycleBinPresenter implements RecycleBinContract.Presenter {

    private RecycleBinContract.View view;
    private OpenHelperDataBase dbHelper;

    public RecycleBinPresenter(Context context) {
        this.dbHelper = new OpenHelperDataBase(context);
    }

    @Override
    public void attachView(RecycleBinContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadDeletedNotes(long userId) {
        if (view != null) {
            List<Note> notes = dbHelper.getAllDeletedNotes(userId);
            if (notes.isEmpty()) {
                view.showEmptyView();
            } else {
                view.showDeletedNotes(notes);
            }
        }
    }

    @Override
    public void restoreNote(long noteId) {
        dbHelper.restoreNote(noteId);
        if (view != null) {
            view.onNoteRestored();
            loadDeletedNotes(view.getUserId());
        }
    }

    @Override
    public void permanentlyDeleteNote(long noteId) {
        dbHelper.permanentlyDeleteNote(noteId);
        if (view != null) {
            view.onNotePermanentlyDeleted();
            loadDeletedNotes(view.getUserId());
        }
    }
} 