package com.Health.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_metric_records",
        indices = {
                @Index(value = {"userId", "metricType", "recordScope", "sampleTime"}),
                @Index(value = {"userId", "metricType", "recordScope", "sourceType", "sampleDay"})
        })
public class HealthMetricEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String metricType = "";

    @NonNull
    public String recordScope = "";

    public Double valuePrimary;
    public Double valueSecondary;

    @NonNull
    public String unit = "";

    @NonNull
    public String sourceType = "";

    public long sampleTime;

    @NonNull
    public String sampleDay = "";

    public long createdAt;
    public long updatedAt;
}
