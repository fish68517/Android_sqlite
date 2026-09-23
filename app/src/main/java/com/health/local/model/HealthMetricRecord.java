package com.Health.local.model;

public class HealthMetricRecord {

    private long id;
    private long userId;
    private String metricType;
    private String recordScope;
    private Double valuePrimary;
    private Double valueSecondary;
    private String unit;
    private String sourceType;
    private long sampleTime;
    private String sampleDay;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getRecordScope() {
        return recordScope;
    }

    public void setRecordScope(String recordScope) {
        this.recordScope = recordScope;
    }

    public Double getValuePrimary() {
        return valuePrimary;
    }

    public void setValuePrimary(Double valuePrimary) {
        this.valuePrimary = valuePrimary;
    }

    public Double getValueSecondary() {
        return valueSecondary;
    }

    public void setValueSecondary(Double valueSecondary) {
        this.valueSecondary = valueSecondary;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public long getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(long sampleTime) {
        this.sampleTime = sampleTime;
    }

    public String getSampleDay() {
        return sampleDay;
    }

    public void setSampleDay(String sampleDay) {
        this.sampleDay = sampleDay;
    }
}
