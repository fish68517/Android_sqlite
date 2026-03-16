package com.hakimi.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hakimi.local.db.entity.DiaryEntity;

import java.util.List;

@Dao
public interface DiaryDao {

    @Insert
    long insert(DiaryEntity diary);

    @Query("SELECT * FROM diaries WHERE userId = :userId ORDER BY id DESC")
    List<DiaryEntity> findByUserId(long userId);

    @Query("SELECT COUNT(1) FROM diaries")
    int count();
}
