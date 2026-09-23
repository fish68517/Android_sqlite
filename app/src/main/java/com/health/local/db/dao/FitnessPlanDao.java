package com.Health.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.Health.local.db.entity.FitnessPlanEntity;

@Dao
public interface FitnessPlanDao {

    @Insert
    long insert(FitnessPlanEntity entity);
}
