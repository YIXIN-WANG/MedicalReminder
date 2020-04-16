package com.example.medicalreminder.model;

public class Clinic {
    private String zipCode, address, name, phoneNumber, apiEndpoint, clinicId;

    public Clinic(String clinicId, String zipCode, String address, String name, String phoneNumber, String apiEndpoint) {
        this.clinicId = clinicId;
        this.zipCode = zipCode;
        this.address = address;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.apiEndpoint = apiEndpoint;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }
}
