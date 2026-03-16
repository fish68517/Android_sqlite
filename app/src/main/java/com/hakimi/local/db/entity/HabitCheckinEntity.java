package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habit_checkins")
public class HabitCheckinEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long habitId;
    public long userId;

    @NonNull
    public String checkinDate = "";

    public long createdAt;
}
