package com.hakimi.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hakimi.local.db.entity.ExerciseRecordEntity;

import java.util.List;

@Dao
public interface ExerciseRecordDao {

    @Insert
    long insert(ExerciseRecordEntity entity);

    @Query("SELECT * FROM exercise_records WHERE userId = :userId ORDER BY id DESC")
    List<ExerciseRecordEntity> findByUserId(long userId);
}
