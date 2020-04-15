package com.example.medicalreminder.model;

public class User {
    private String email;
    private String careGiverEmail;
    private String ohipNumber;
    private long loginTime;

    public User(){}

    public User(String email, String careGiverEmail, long loginTime, String ohipNumber) {
        this.email = email;
        this.careGiverEmail = careGiverEmail;
        this.loginTime = loginTime;
        this.ohipNumber = ohipNumber;
    }

    public String getOhipNumber() {
        return ohipNumber;
    }

    public void setOhipNumber(String ohipNumber) {
        this.ohipNumber = ohipNumber;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCareGiverEmail() {
        return careGiverEmail;
    }

    public void setCareGiverEmail(String careGiverEmail) {
        this.careGiverEmail = careGiverEmail;
    }
}
