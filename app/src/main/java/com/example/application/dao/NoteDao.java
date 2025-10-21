package com.example.application.dao;// =================================================================================
// 文件路径: app/src/main/java/com/example/application/data/local/NoteDao.java
// 任务: MVVM 架构 (Room) - 级别 3
// 描述: 数据访问对象(DAO)，定义了所有与notes表交互的数据库操作。
// =================================================================================

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.application.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();
}