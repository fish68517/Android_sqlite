package com.Health.local.model;

public class HealthAlertItem {

    private long id;
    private long userId;
    private String metricType;
    private String alertKey;
    private String alertLevel;
    private String title;
    private String content;
    private Double valuePrimary;
    private Double valueSecondary;
    private String sourceType;
    private boolean acknowledged;
    private long createdAt;

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

    public String getAlertKey() {
        return alertKey;
    }

    public void setAlertKey(String alertKey) {
        this.alertKey = alertKey;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
