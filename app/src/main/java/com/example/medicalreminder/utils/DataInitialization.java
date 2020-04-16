package com.example.medicalreminder.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.medicalreminder.model.Clinic;
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
}
