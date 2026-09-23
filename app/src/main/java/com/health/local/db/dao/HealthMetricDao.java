package com.Health.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Health.local.db.entity.HealthMetricEntity;

import java.util.List;

@Dao
public interface HealthMetricDao {

    @Insert
    long insert(HealthMetricEntity entity);

    @Update
    int update(HealthMetricEntity entity);

    @Query("SELECT * FROM health_metric_records WHERE userId = :userId AND metricType = :metricType "
            + "AND recordScope = :recordScope ORDER BY sampleTime DESC LIMIT 1")
    HealthMetricEntity latestByType(long userId, String metricType, String recordScope);

    @Query("SELECT * FROM health_metric_records WHERE userId = :userId AND metricType = :metricType "
            + "AND recordScope = :recordScope AND sourceType = :sourceType AND sampleDay = :sampleDay "
            + "ORDER BY sampleTime DESC LIMIT 1")
    HealthMetricEntity findDaily(long userId, String metricType, String recordScope,
            String sourceType, String sampleDay);

    @Query("SELECT * FROM health_metric_records WHERE userId = :userId AND metricType = :metricType "
            + "AND recordScope = :recordScope AND sampleTime BETWEEN :startTime AND :endTime "
            + "ORDER BY sampleTime ASC")
    List<HealthMetricEntity> listRange(long userId, String metricType, String recordScope,
            long startTime, long endTime);

    @Query("SELECT * FROM health_metric_records WHERE userId = :userId AND metricType = :metricType "
            + "ORDER BY sampleTime DESC LIMIT :limit")
    List<HealthMetricEntity> listRecent(long userId, String metricType, int limit);
}
