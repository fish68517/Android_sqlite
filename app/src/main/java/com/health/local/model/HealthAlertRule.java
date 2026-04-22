package com.Health.local.model;

public class HealthAlertRule {

    private long userId;
    private int heartRateLow;
    private int heartRateHigh;
    private int systolicLow;
    private int systolicHigh;
    private int diastolicLow;
    private int diastolicHigh;
    private int stepGoal;
    private int stepAlertThreshold;
    private boolean voiceEnabled;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getHeartRateLow() {
        return heartRateLow;
    }

    public void setHeartRateLow(int heartRateLow) {
        this.heartRateLow = heartRateLow;
    }

    public int getHeartRateHigh() {
        return heartRateHigh;
    }

    public void setHeartRateHigh(int heartRateHigh) {
        this.heartRateHigh = heartRateHigh;
    }

    public int getSystolicLow() {
        return systolicLow;
    }

    public void setSystolicLow(int systolicLow) {
        this.systolicLow = systolicLow;
    }

    public int getSystolicHigh() {
        return systolicHigh;
    }

    public void setSystolicHigh(int systolicHigh) {
        this.systolicHigh = systolicHigh;
    }

    public int getDiastolicLow() {
        return diastolicLow;
    }

    public void setDiastolicLow(int diastolicLow) {
        this.diastolicLow = diastolicLow;
    }

    public int getDiastolicHigh() {
        return diastolicHigh;
    }

    public void setDiastolicHigh(int diastolicHigh) {
        this.diastolicHigh = diastolicHigh;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getStepAlertThreshold() {
        return stepAlertThreshold;
    }

    public void setStepAlertThreshold(int stepAlertThreshold) {
        this.stepAlertThreshold = stepAlertThreshold;
    }

    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }

    public void setVoiceEnabled(boolean voiceEnabled) {
        this.voiceEnabled = voiceEnabled;
    }
}
