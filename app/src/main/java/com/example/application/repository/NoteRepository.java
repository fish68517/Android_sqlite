package com.example.application.repository;// =================================================================================
// 文件路径: app/src/main/java/com/example/application/data/repository/NoteRepository.java
// 任务: MVVM 架构 - 级别 3
// 描述: Repository层，作为数据来源的唯一入口，隔离了ViewModel和数据源。
// =================================================================================


import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.application.NoteDatabase;
import com.example.application.dao.NoteDao;
import com.example.application.model.Note;

import java.util.List;

public class NoteRepository {
    private final NoteDao mNoteDao;
    private final LiveData<List<Note>> mAllNotes;

    public NoteRepository(Application application) {
        NoteDatabase db = NoteDatabase.getDatabase(application);
        mNoteDao = db.noteDao();
        mAllNotes = mNoteDao.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public void insert(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> mNoteDao.insert(note));
    }

    public void delete(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> mNoteDao.delete(note));
    }
}