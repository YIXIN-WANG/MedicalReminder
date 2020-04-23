package com.example.medicalreminder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.medicalreminder.model.Clinic;
import com.example.medicalreminder.model.Medicine;
import com.example.medicalreminder.model.Reminder;
import com.example.medicalreminder.service.NotificationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataInitialization {

    public static void initData(String userId, Context context){
        //String clinic_id = initClinics();
        DatabaseReference dbClinicRef = FirebaseDatabase.getInstance().getReference("Clinics");
        dbClinicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot clinicSnapshot : dataSnapshot.getChildren()){
                    Clinic clinic = clinicSnapshot.getValue(Clinic.class);
                    if(clinic.getPhoneNumber().equals("519-208-1348")){
                        String medId = initMedicine(clinic.getClinicId(), userId);
                        initReminder(userId, medId, context);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Retrieve Clinics Error:",databaseError.getMessage());
            }
        });

    }

    private static String initClinics(){
        DatabaseReference clinicRef = FirebaseDatabase.getInstance().getReference("Clinics");
        String id_1 = clinicRef.push().getKey();
        String id_2 = clinicRef.push().getKey();

        Clinic clinic1 = new Clinic(id_1, "N2L 3E9", "170 University Ave W, Waterloo, ON",
                "Waterloo Walk-In Clinic", "519-725-1514", "http://waterclinic.com/api/medicalreminder/v1");

        Clinic clinic2 = new Clinic(id_2, "N2L 6N9", "50 Westmount Rd N, Waterloo, ON",
                "Westmount Place Walk In Clinic", "519-954-0111", "http://westmountclinic.com/api/medicalreminder/v1");

        setClinic(clinicRef, clinic1);
        setClinic(clinicRef, clinic2);
        return id_1;
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

    private static String initMedicine(String clinicId, String userId){
        DatabaseReference medRef = FirebaseDatabase.getInstance().getReference("Medicine");
        String id_1 = medRef.push().getKey();
        String id_2 = medRef.push().getKey();

        try {
            long expiryDate1 = (new SimpleDateFormat("yyyy-MM-dd").parse("2021-12-20")).getTime();
            long expiryDate2 = (new SimpleDateFormat("yyyy-MM-dd").parse("2019-10-23")).getTime();
            Medicine med1 = new Medicine(id_1, "Trandolapril", "7660502", "Take one Capsule by mouse daily with 8 oz glass of water"
                    ,"John Doe", userId, clinicId,
                    30, 10,expiryDate1, 6);

            Medicine med2 = new Medicine(id_2, "Trandolapril", "7616492", "Take one Capsule by mouse daily with 8 oz glass of water"
                    ,"Ethan Walker", userId, clinicId,
                    30, 5,expiryDate2, 6);

            setMedicine(medRef,med1);
            setMedicine(medRef,med2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return id_1;
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

    private static void initReminder(String userId, String medicineId, Context context){
        DatabaseReference remRef = FirebaseDatabase.getInstance().getReference("Reminder");
        LocalDateTime today = LocalDateTime.now();
        NotificationServices ns = new NotificationServices(context);
        for(int i = 0; i < 8; i++){
            String id1 = remRef.push().getKey();
            String id2 = remRef.push().getKey();
            LocalDateTime t1 = today.plusDays(i).withHour(10).withMinute(0).withSecond(0);
            LocalDateTime t2 = today.plusDays(i).withHour(16).withMinute(0).withSecond(0);
            if(i == 0){
                t1 = today.plusMinutes(2);
                t2 = t1.plusHours(6);
                ns.scheduleNotification(ns.getNotification("Time to take pills!"), 120*1000);
                //todo: Schedule notification here, use t1.
            }
            long scheduleTime1 = ZonedDateTime.of(t1, ZoneId.systemDefault()).toInstant().toEpochMilli();
            long scheduleTime2 = ZonedDateTime.of(t2, ZoneId.systemDefault()).toInstant().toEpochMilli();
            Reminder rem1 = new Reminder(id1, scheduleTime1, 0L, medicineId, userId,
                    false, 1);
            Reminder rem2 = new Reminder(id2, scheduleTime2, 0L, medicineId, userId,
                    false, 2);
            setReminder(remRef, rem1);
            setReminder(remRef, rem2);
        }

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
