package com.example.medicalreminder.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalreminder.R;
import com.example.medicalreminder.model.Clinic;
import com.example.medicalreminder.model.Medicine;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MedicineItemListAdapter extends RecyclerView.Adapter<MedicineItemListAdapter.MedListViewHolder> {

    private List<Medicine> medicineList;
    private LayoutInflater layoutInflater;
    private DatabaseReference dbClinicRef;

    public MedicineItemListAdapter(Context context, List<Medicine> medicineList) {
        layoutInflater = LayoutInflater.from(context);
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.medicine_item, parent, false);
        dbClinicRef = FirebaseDatabase.getInstance().getReference("Clinics");
        return new MedListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedListViewHolder holder, int position) {
        final int itemPosition = position;
        final Medicine medicineItem = medicineList.get(position);

        holder.medNameTextView.setText(medicineItem.getName());
        Date expiryDate = new Date(medicineItem.getExpiryDate());
        holder.medExpiryDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(expiryDate));
        if(expiryDate.before(new Date())){
            holder.medExpiryDateTextView.setTextColor(Color.RED);
        }

        holder.medPrescriptionNumber.setText(medicineItem.getPrescriptionNumber());
        holder.medDirectionTextView.setText(medicineItem.getDirection());
        holder.medQuantityTextView.setText(String.valueOf(medicineItem.getQuantity())+" tablets");
        holder.medRefillTextView.setText(medicineItem.getRefillsRemaining() + " of 12");

        dbClinicRef.child(medicineItem.getClinicId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Clinic clinic = dataSnapshot.getValue(Clinic.class);
                holder.medClinicTextView.setText(clinic.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Med Clinics Error:",databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public class MedListViewHolder extends RecyclerView.ViewHolder{
        TextView medNameTextView;
        TextView medExpiryDateTextView;
        TextView medPrescriptionNumber;
        TextView medDirectionTextView;
        TextView medDoctorTextView;
        TextView medRefillTextView;
        TextView medQuantityTextView;
        TextView medClinicTextView;

        MedListViewHolder(View itemView) {
            super(itemView);
            medNameTextView = itemView.findViewById(R.id.medicineName);
            medExpiryDateTextView = itemView.findViewById(R.id.medExpiryDate);
            medPrescriptionNumber = itemView.findViewById(R.id.medPrescriptionNum);
            medDirectionTextView = itemView.findViewById(R.id.medDirection);
            medDoctorTextView = itemView.findViewById(R.id.medDoctor);
            medRefillTextView = itemView.findViewById(R.id.medRefills);
            medQuantityTextView = itemView.findViewById(R.id.medQuantity);
            medClinicTextView = itemView.findViewById(R.id.medClinic);
        }
    }

}
