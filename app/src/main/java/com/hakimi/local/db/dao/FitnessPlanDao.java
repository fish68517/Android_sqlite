package com.hakimi.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.hakimi.local.db.entity.FitnessPlanEntity;

@Dao
public interface FitnessPlanDao {

    @Insert
    long insert(FitnessPlanEntity entity);
}
