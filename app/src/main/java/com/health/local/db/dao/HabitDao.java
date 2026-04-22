package com.Health.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.Health.local.db.entity.HabitCheckinEntity;
import com.Health.local.db.entity.HabitEntity;

import java.util.List;

@Dao
public interface HabitDao {

    @Insert
    long insertHabit(HabitEntity habit);

    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY id DESC")
    List<HabitEntity> findByUserId(long userId);

    @Insert
    long insertCheckin(HabitCheckinEntity checkin);

    @Query("SELECT COUNT(1) FROM habit_checkins WHERE habitId = :habitId AND checkinDate = :checkinDate")
    int checkinCountByDate(long habitId, String checkinDate);
}
