package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "community_posts")
public class CommunityPostEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String content = "";

    public String imagePath;
    public int likesCount;

    @NonNull
    public String createdAt = "";

    @NonNull
    public String updatedAt = "";
}
