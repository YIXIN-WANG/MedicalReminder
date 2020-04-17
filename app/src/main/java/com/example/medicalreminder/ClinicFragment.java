package com.example.medicalreminder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medicalreminder.adapter.ClinicItemListAdapter;
import com.example.medicalreminder.model.Clinic;
import com.example.medicalreminder.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClinicFragment extends Fragment implements ClinicItemListAdapter.OnClinicItemListener {

    private List<Clinic> clinicList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClinicItemListAdapter clinicItemListAdapter;
    private DatabaseReference dbClinicRef;

    public ClinicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic, container, false);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("Clinics");
        recyclerView = view.findViewById(R.id.clinic_recycleView);

        recyclerView.setHasFixedSize(true);
        clinicItemListAdapter = new ClinicItemListAdapter(getActivity(), clinicList, this);
        recyclerView.setAdapter(clinicItemListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbClinicRef = FirebaseDatabase.getInstance().getReference("Clinics");
        dbClinicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clinicList.clear();
                for(DataSnapshot clinicSnapshot : dataSnapshot.getChildren()){
                    Clinic clinic = clinicSnapshot.getValue(Clinic.class);
                    clinicList.add(clinic);
                }
                clinicItemListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Retrieve Clinics Error:",databaseError.getMessage());
            }
        });

        return view;
    }


    @Override
    public void onOpenDirectionClick(Clinic clinic) {

    }

    @Override
    public void onOpenPhoneClick(Clinic clinic) {

    }
}
