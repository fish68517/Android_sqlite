package com.hakimi.local.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medication_reminders")
public class MedicationReminderEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;

    @NonNull
    public String medicineName = "";

    @NonNull
    public String dosage = "";

    // HH:mm
    @NonNull
    public String reminderTime = "";

    @NonNull
    public String lastTakenDate = "";

    public int enabled;
    public long createdAt;
    public long updatedAt;
}
