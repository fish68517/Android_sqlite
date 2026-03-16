package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String username = "";

    @NonNull
    public String phone = "";

    @NonNull
    public String password = "";

    public String email;
    public Double height;
    public Double weight;
    public String avatar;
    public long createdAt;
    public long updatedAt;
}
