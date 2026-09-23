package com.Health.local.model;

public class HealthCheckinSummary {

    private int waterCount;
    private int medicineCount;
    private int weightCount;
    private String latestWeight;
    private int habitCount;
    private int habitCheckedTodayCount;
    private int reminderCount;
    private int reminderEnabledCount;

    public int getWaterCount() {
        return waterCount;
    }

    public void setWaterCount(int waterCount) {
        this.waterCount = waterCount;
    }

    public int getMedicineCount() {
        return medicineCount;
    }

    public void setMedicineCount(int medicineCount) {
        this.medicineCount = medicineCount;
    }

    public int getWeightCount() {
        return weightCount;
    }

    public void setWeightCount(int weightCount) {
        this.weightCount = weightCount;
    }

    public String getLatestWeight() {
        return latestWeight;
    }

    public void setLatestWeight(String latestWeight) {
        this.latestWeight = latestWeight;
    }

    public int getHabitCount() {
        return habitCount;
    }

    public void setHabitCount(int habitCount) {
        this.habitCount = habitCount;
    }

    public int getHabitCheckedTodayCount() {
        return habitCheckedTodayCount;
    }

    public void setHabitCheckedTodayCount(int habitCheckedTodayCount) {
        this.habitCheckedTodayCount = habitCheckedTodayCount;
    }

    public int getReminderCount() {
        return reminderCount;
    }

    public void setReminderCount(int reminderCount) {
        this.reminderCount = reminderCount;
    }

    public int getReminderEnabledCount() {
        return reminderEnabledCount;
    }

    public void setReminderEnabledCount(int reminderEnabledCount) {
        this.reminderEnabledCount = reminderEnabledCount;
    }
}
