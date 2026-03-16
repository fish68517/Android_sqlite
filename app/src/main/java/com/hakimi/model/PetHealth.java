package com.hakimi.model;

import java.io.Serializable;

/**
 * 宠物健康档案
 * 
 * @author hakimi
 */
public class PetHealth implements Serializable {
    private String healthRecord;
    private String medicalHistory;
    private String allergies;
    private String diet;
    private String exerciseHabit;

    public PetHealth() {
    }

    // Getters and Setters
    public String getHealthRecord() {
        return healthRecord;
    }

    public void setHealthRecord(String healthRecord) {
        this.healthRecord = healthRecord;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getExerciseHabit() {
        return exerciseHabit;
    }

    public void setExerciseHabit(String exerciseHabit) {
        this.exerciseHabit = exerciseHabit;
    }

    @Override
    public String toString() {
        return "PetHealth{" +
                "healthRecord='" + healthRecord + '\'' +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", allergies='" + allergies + '\'' +
                ", diet='" + diet + '\'' +
                ", exerciseHabit='" + exerciseHabit + '\'' +
                '}';
    }
}

