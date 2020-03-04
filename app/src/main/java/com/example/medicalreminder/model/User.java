package com.example.medicalreminder.model;

public class User {
    private String email;
    private String careGiverEmail;

    public User(){}

    public User(String email, String careGiverEmail) {
        this.email = email;
        this.careGiverEmail = careGiverEmail;
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
