package com.example.medicalreminder.model;

public class Medicine {
    private String name, prescriptionNumber, direction, doctorName, userId, clinicId, medicineId;
    private int quantity;
    private int refillsRemaining;
    private int takeInterval;
    private long expiryDate;

    public Medicine(){}

    public Medicine(String medicineId, String name, String prescriptionNumber, String direction, String doctorName, String userId, String clinicId, int quantity, int refillsRemaining, long expiryDate, int takeInterval) {
        this.medicineId = medicineId;
        this.name = name;
        this.prescriptionNumber = prescriptionNumber;
        this.direction = direction;
        this.doctorName = doctorName;
        this.userId = userId;
        this.clinicId = clinicId;
        this.quantity = quantity;
        this.refillsRemaining = refillsRemaining;
        this.expiryDate = expiryDate;
        this.takeInterval = takeInterval;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRefillsRemaining() {
        return refillsRemaining;
    }

    public void setRefillsRemaining(int refillsRemaining) {
        this.refillsRemaining = refillsRemaining;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getTakeInterval() {
        return takeInterval;
    }

    public void setTakeInterval(int takeInterval) {
        this.takeInterval = takeInterval;
    }
}
