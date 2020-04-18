package com.example.medicalreminder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.medicalreminder.model.Clinic;
import com.example.medicalreminder.model.Medicine;
import com.example.medicalreminder.model.Reminder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataInitialization {

    public static void initClinics(){
        DatabaseReference clinicRef = FirebaseDatabase.getInstance().getReference("Clinics");
        String id_1 = clinicRef.push().getKey();
        String id_2 = clinicRef.push().getKey();

        Clinic clinic1 = new Clinic(id_1, "N2L 3E9", "170 University Ave W, Waterloo, ON",
                "Waterloo Walk-In Clinic", "519-725-1514", "http://waterclinic.com/api/medicalreminder/v1");

        Clinic clinic2 = new Clinic(id_2, "N2L 6N9", "50 Westmount Rd N, Waterloo, ON",
                "Westmount Place Walk In Clinic", "519-954-0111", "http://westmountclinic.com/api/medicalreminder/v1");

        setClinic(clinicRef, clinic1);
        setClinic(clinicRef, clinic2);
    }

    private static void setClinic(DatabaseReference dbRef, final Clinic clinic){
        dbRef.child(clinic.getClinicId())
                .setValue(clinic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Init Clinic",clinic.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Init Clinic Failed", e.getMessage());
            }
        });
    }

    public static void initMedicine(){
        DatabaseReference medRef = FirebaseDatabase.getInstance().getReference("Medicine");
        String id_1 = medRef.push().getKey();

        String medName = "Trandolapril";
        String prescNum = "98761234";
        String direct = "One pill a day with or without food.";
        String docName = "Dr. John Doe";
        String userID = "4eitjJEqPgVz37A7YPIMwouc8FC3";
        String clinicID = "-M548ZnT0FUlZDuqgYgv";
        int quantity = 1;
        int refillsRemaining = 0;
        long expiryDate = 0L;
        String string_date = "12-December-2021";
        int takeInterval = 6;
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date d = f.parse(string_date);
            expiryDate = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Medicine med1 = new Medicine(id_1, medName, prescNum, direct,docName,userID,clinicID,
                quantity, refillsRemaining,expiryDate, takeInterval);

        setMedicine(medRef,med1);
    }

    private static void setMedicine(DatabaseReference dbRef, final Medicine med){
        dbRef.child(med.getMedicineId())
                .setValue(med).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Init Med",med.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Init Med Failed", e.getMessage());
            }
        });
    }

    public static void initReminder(){
        DatabaseReference remRef = FirebaseDatabase.getInstance().getReference("Reminder");
        String id_1 = remRef.push().getKey();

        String reminderID = id_1;
        String userID = "4eitjJEqPgVz37A7YPIMwouc8FC3";
        String medicineID = "";
        long scheduleTime = 0L;
        long takeTime = 0L;
        long sequence = 0L;
        boolean takenMed = false;

        String string_date_schedule = "2020/04/18 21:00:00";

        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date d = f.parse(string_date_schedule);
            scheduleTime = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Reminder rem1 = new Reminder(reminderID, scheduleTime, takeTime, medicineID, userID,
                takenMed, sequence);

        setReminder(remRef, rem1);
    }
    private static void setReminder(DatabaseReference dbRef, final Reminder rem){
        dbRef.child(rem.getReminderId())
                .setValue(rem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Init Rem",rem.getReminderId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Init Rem Failed", e.getMessage());
            }
        });
    }
}
