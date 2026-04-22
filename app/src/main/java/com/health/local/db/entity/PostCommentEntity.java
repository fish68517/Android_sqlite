package com.Health.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post_comments")
public class PostCommentEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long postId;
    public long userId;

    @NonNull
    public String content = "";

    @NonNull
    public String createdAt = "";

    @NonNull
    public String updatedAt = "";
}
