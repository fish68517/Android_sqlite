package com.Health.local.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_alert_rules")
public class HealthAlertRuleEntity {

    @PrimaryKey
    public long userId;

    public int heartRateLow;
    public int heartRateHigh;
    public int systolicLow;
    public int systolicHigh;
    public int diastolicLow;
    public int diastolicHigh;
    public int stepGoal;
    public int stepAlertThreshold;
    public int voiceEnabled;
    public long updatedAt;
}
