package com.Health.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_alert_records",
        indices = {
                @Index(value = {"userId", "createdAt"}),
                @Index(value = {"userId", "alertKey"})
        })
public class HealthAlertEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String metricType = "";

    @NonNull
    public String alertKey = "";

    @NonNull
    public String alertLevel = "";

    @NonNull
    public String title = "";

    @NonNull
    public String content = "";

    public Double valuePrimary;
    public Double valueSecondary;

    @NonNull
    public String sourceType = "";

    public int acknowledged;
    public long createdAt;
}
