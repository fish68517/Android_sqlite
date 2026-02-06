package com.example.healthdietapp.models;

/**
 * HealthRecord Model - Represents daily health data records
 */
public class HealthRecord {
    private String recordId;
    private String userId;
    private String date;
    private float weight;
    private float waterIntake;
    private String measurements;
    private long recordedAt;

    public HealthRecord() {
    }

    public HealthRecord(String recordId, String userId, String date) {
        this.recordId = recordId;
        this.userId = userId;
        this.date = date;
        this.recordedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(float waterIntake) {
        this.waterIntake = waterIntake;
    }

    public String getMeasurements() {
        return measurements;
    }

    public void setMeasurements(String measurements) {
        this.measurements = measurements;
    }

    public long getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(long recordedAt) {
        this.recordedAt = recordedAt;
    }
}
