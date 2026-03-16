package com.hakimi.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hakimi.local.db.entity.MedicationReminderEntity;

import java.util.List;

@Dao
public interface MedicationReminderDao {

    @Insert
    long insert(MedicationReminderEntity entity);

    @Update
    int update(MedicationReminderEntity entity);

    @Query("SELECT * FROM medication_reminders WHERE userId = :userId AND enabled = 1 ORDER BY reminderTime ASC")
    List<MedicationReminderEntity> enabledByUser(long userId);

    @Query("SELECT * FROM medication_reminders WHERE userId = :userId ORDER BY reminderTime ASC")
    List<MedicationReminderEntity> allByUser(long userId);

    @Query("SELECT * FROM medication_reminders WHERE id = :id LIMIT 1")
    MedicationReminderEntity findById(long id);

    @Query("UPDATE medication_reminders SET enabled = :enabled, updatedAt = :updatedAt WHERE id = :id")
    int updateEnabled(long id, int enabled, long updatedAt);

    @Query("DELETE FROM medication_reminders WHERE id = :id")
    int deleteById(long id);
}
