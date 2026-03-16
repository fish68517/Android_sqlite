package com.hakimi.local.model;

public class MedicationReminderItem {

    private long id;
    private String medicineName;
    private String dosage;
    private String reminderTime;
    private String lastTakenDate;
    private boolean enabled;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getLastTakenDate() {
        return lastTakenDate;
    }

    public void setLastTakenDate(String lastTakenDate) {
        this.lastTakenDate = lastTakenDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
