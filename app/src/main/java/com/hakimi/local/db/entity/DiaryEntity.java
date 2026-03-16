package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diaries")
public class DiaryEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String content = "";

    public int mood;

    @NonNull
    public String createdAt = "";

    @NonNull
    public String updatedAt = "";
}
