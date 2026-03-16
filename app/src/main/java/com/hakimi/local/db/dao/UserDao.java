package com.hakimi.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hakimi.local.db.entity.UserEntity;

@Dao
public interface UserDao {

    @Insert
    long insert(UserEntity user);

    @Update
    int update(UserEntity user);

    @Query("SELECT * FROM users WHERE (username = :account OR phone = :account) LIMIT 1")
    UserEntity findByAccount(String account);

    @Query("SELECT * FROM users WHERE (username = :username OR phone = :phone) LIMIT 1")
    UserEntity findDuplicated(String username, String phone);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity findById(long id);

    @Query("SELECT COUNT(1) FROM users")
    int count();
}
