package com.Health.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.Health.local.db.entity.QuickRecordEntity;

import java.util.List;

@Dao
public interface QuickRecordDao {

    @Insert
    long insert(QuickRecordEntity entity);

    @Query("SELECT COUNT(1) FROM quick_records WHERE userId = :userId AND type = :type")
    int countByType(long userId, String type);

    @Query("SELECT * FROM quick_records WHERE userId = :userId ORDER BY id DESC LIMIT :limit")
    List<QuickRecordEntity> latestByUser(long userId, int limit);

    @Query("SELECT * FROM quick_records WHERE userId = :userId AND type = :type ORDER BY id DESC LIMIT 1")
    QuickRecordEntity latestByType(long userId, String type);
}
