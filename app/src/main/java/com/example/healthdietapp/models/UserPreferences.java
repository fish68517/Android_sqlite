package com.example.healthdietapp.models;

/**
 * UserPreferences Model - Represents user dietary preferences
 */
public class UserPreferences {
    private String preferenceId;
    private String userId;
    private String tasteTendency;
    private String dietType;
    private String healthGoal;
    private String restrictions;

    public UserPreferences() {
    }

    public UserPreferences(String preferenceId, String userId) {
        this.preferenceId = preferenceId;
        this.userId = userId;
    }

    // Getters and Setters
    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTasteTendency() {
        return tasteTendency;
    }

    public void setTasteTendency(String tasteTendency) {
        this.tasteTendency = tasteTendency;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public String getHealthGoal() {
        return healthGoal;
    }

    public void setHealthGoal(String healthGoal) {
        this.healthGoal = healthGoal;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }
}
