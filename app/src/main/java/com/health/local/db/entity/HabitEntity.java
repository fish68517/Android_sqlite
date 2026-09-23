package com.Health.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class HabitEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String name = "";

    public long createdAt;
}
