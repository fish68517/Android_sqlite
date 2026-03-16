package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercise_records")
public class ExerciseRecordEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String exerciseType = "";

    @NonNull
    public String location = "";

    public int duration;

    @NonNull
    public String createdAt = "";

    @NonNull
    public String updatedAt = "";
}
