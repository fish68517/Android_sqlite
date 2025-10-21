package com.example.application.viewmodel;// =================================================================================
// 文件路径: app/src/main/java/com/example/application/viewmodel/NoteViewModel.java
// 任务: MVVM 架构 - 级别 3
// 描述: ViewModel层，持有UI数据，并在配置更改后存活。
// =================================================================================


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.application.model.Note;
import com.example.application.repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepository mRepository;
    private final LiveData<List<Note>> mAllNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public void insert(Note note) {
        mRepository.insert(note);
    }

    public void delete(Note note) {
        mRepository.delete(note);
    }

    public void update(Note note) {
        mRepository.update(note);
    }
}
