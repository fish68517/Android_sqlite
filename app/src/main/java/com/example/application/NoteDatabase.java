package com.example.application;// =================================================================================
// 文件路径: app/src/main/java/com/example/geeknotes/data/local/NoteDatabase.java
// 任务: MVVM 架构 (Room) - 级别 3
// 描述: Room数据库的主类。
// =================================================================================


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.application.dao.NoteDao;
import com.example.application.model.Note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();

    private static volatile NoteDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public static NoteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NoteDatabase.class, "note_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}