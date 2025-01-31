package com.example.medicalreminder.model;

import java.io.Serializable;

public class Reminder implements Serializable {
    private long scheduleTime, takeTime;
    private String medicineId, userId, reminderId;

    private long sequence;
    private boolean takenMed;

    public Reminder(){}

    public Reminder(String reminderId, long scheduleTime, long takeTime, String medicineId, String userId, boolean takenMed, long sequence) {
        this.reminderId = reminderId;
        this.scheduleTime = scheduleTime;
        this.takeTime = takeTime;
        this.medicineId = medicineId;
        this.userId = userId;
        this.takenMed = takenMed;
        this.sequence = sequence;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public long getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(long takeTime) {
        this.takeTime = takeTime;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isTakenMed() {
        return takenMed;
    }

    public void setTakenMed(boolean takenMed) {
        this.takenMed = takenMed;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
