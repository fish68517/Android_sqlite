package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fitness_plans")
public class FitnessPlanEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String goal = "";

    @NonNull
    public String planContent = "";

    @NonNull
    public String createdAt = "";

    @NonNull
    public String updatedAt = "";
}
