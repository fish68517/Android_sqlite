package com.Health.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Health.local.db.entity.HealthAlertRuleEntity;

@Dao
public interface HealthAlertRuleDao {

    @Query("SELECT * FROM health_alert_rules WHERE userId = :userId LIMIT 1")
    HealthAlertRuleEntity findByUserId(long userId);

    @Insert
    long insert(HealthAlertRuleEntity entity);

    @Update
    int update(HealthAlertRuleEntity entity);
}
