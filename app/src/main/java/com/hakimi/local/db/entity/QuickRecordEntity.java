package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quick_records")
public class QuickRecordEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String type = "";

    public String value;
    public String note;
    public long createdAt;
}
