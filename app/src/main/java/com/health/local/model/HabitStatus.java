package com.Health.local.model;

public class HabitStatus {

    private long habitId;
    private String habitName;
    private boolean checkedToday;

    public long getHabitId() {
        return habitId;
    }

    public void setHabitId(long habitId) {
        this.habitId = habitId;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public boolean isCheckedToday() {
        return checkedToday;
    }

    public void setCheckedToday(boolean checkedToday) {
        this.checkedToday = checkedToday;
    }
}
