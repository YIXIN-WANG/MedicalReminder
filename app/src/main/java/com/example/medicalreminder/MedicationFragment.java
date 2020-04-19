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
import com.example.medicalreminder.adapter.MedicineItemListAdapter;
import com.example.medicalreminder.model.Clinic;
import com.example.medicalreminder.model.Medicine;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MedicationFragment extends Fragment {

    private List<Medicine> medicineList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MedicineItemListAdapter medicineItemListAdapter;
    private DatabaseReference dbMedRef;

    public MedicationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medication, container, false);

        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("Medication");
        recyclerView = view.findViewById(R.id.medicine_recycleView);

        recyclerView.setHasFixedSize(true);
        medicineItemListAdapter = new MedicineItemListAdapter(getActivity(), medicineList);
        recyclerView.setAdapter(medicineItemListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbMedRef = FirebaseDatabase.getInstance().getReference("Medicine");
        dbMedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicineList.clear();
                for(DataSnapshot clinicSnapshot : dataSnapshot.getChildren()){
                    Medicine medicine = clinicSnapshot.getValue(Medicine.class);
                    medicineList.add(medicine);
                }
                medicineItemListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Retrieve Med Error:",databaseError.getMessage());
            }
        });

        return view;
    }
}
