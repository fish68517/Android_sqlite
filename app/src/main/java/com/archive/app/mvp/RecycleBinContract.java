package com.archive.app.mvp;

import com.archive.app.model.Note;
import java.util.List;

public interface RecycleBinContract {

    interface View {
        void showDeletedNotes(List<Note> notes);
        void showEmptyView();
        void onNoteRestored();
        void onNotePermanentlyDeleted();
        long getUserId();
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadDeletedNotes(long userId);
        void restoreNote(long noteId);
        void permanentlyDeleteNote(long noteId);
    }
} 