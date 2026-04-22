package com.Health.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Health.local.db.entity.HealthAlertEntity;

import java.util.List;

@Dao
public interface HealthAlertDao {

    @Insert
    long insert(HealthAlertEntity entity);

    @Update
    int update(HealthAlertEntity entity);

    @Query("SELECT * FROM health_alert_records WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    List<HealthAlertEntity> latestByUser(long userId, int limit);

    @Query("SELECT * FROM health_alert_records WHERE userId = :userId AND alertKey = :alertKey "
            + "ORDER BY createdAt DESC LIMIT 1")
    HealthAlertEntity latestByKey(long userId, String alertKey);

    @Query("SELECT COUNT(1) FROM health_alert_records WHERE userId = :userId AND acknowledged = 0")
    int countUnacknowledged(long userId);

    @Query("UPDATE health_alert_records SET acknowledged = 1 WHERE userId = :userId")
    int markAllAcknowledged(long userId);
}
