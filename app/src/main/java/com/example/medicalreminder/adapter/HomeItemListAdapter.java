package com.example.medicalreminder.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalreminder.HomeFragment;
import com.example.medicalreminder.R;
import com.example.medicalreminder.model.Medicine;
import com.example.medicalreminder.model.Reminder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeItemListAdapter extends RecyclerView.Adapter<HomeItemListAdapter.HomeListViewHolder> {

    private List<Reminder> remList;
    private LayoutInflater layoutInflater;
    private HomeFragment homeFragment;
    private DatabaseReference medRef;
    private DatabaseReference remRef;

    public HomeItemListAdapter(Context context, List<Reminder> remList, HomeFragment homeFragment) {
        layoutInflater = LayoutInflater.from(context);
        this.remList = remList;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public HomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.home_item, parent, false);
        medRef = FirebaseDatabase.getInstance().getReference("Medicine");
        remRef = FirebaseDatabase.getInstance().getReference("Reminder");
        return new HomeListViewHolder(view, homeFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListViewHolder holder, int position) {
        final int itemPosition = position;
        final Reminder remItem = remList.get(position);
        if(remItem.isTakenMed()){
            Instant instant = Instant.ofEpochMilli(remItem.getTakeTime());
            LocalDateTime takenTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            String histTime = "Taken at "+takenTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' hh:mm a"));
            holder.medTakenTime.setText(histTime);
            holder.medTakenTime.setVisibility(View.VISIBLE);
        }
        else{
            holder.medTakenTime.setVisibility(View.INVISIBLE);
        }

        Long timeHist = remItem.getScheduleTime();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String timeFormated = formatter.format(timeHist);
        holder.timeMedSchedule.setText(timeFormated);

        Query q1 = medRef.child(remItem.getMedicineId());
        q1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Medicine med = dataSnapshot.getValue(Medicine.class);
                holder.medNameTitle.setText(med.getName());
                holder.instructionText.setText(med.getDirection());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Medicine query error:", databaseError.getMessage());
            }
        });

        holder.openTimePickerTextView.setOnClickListener(view -> {
            holder.onClinicItemListener.onOpenTextClick(remList.get(itemPosition));
        });
    }

    @Override
    public int getItemCount() {
        return remList.size();
    }

    public class HomeListViewHolder extends RecyclerView.ViewHolder{
        TextView medNameTitle;
        TextView timeMedSchedule;
        TextView instructionText;
        TextView medTakenTime;
        TextView openTimePickerTextView;
        private OnReminderItemListener onClinicItemListener;

        HomeListViewHolder(View itemView, OnReminderItemListener onClinicItemListener) {
            super(itemView);
            this.onClinicItemListener = onClinicItemListener;
            medNameTitle = itemView.findViewById(R.id.medNameTitle);
            timeMedSchedule = itemView.findViewById(R.id.timeMedSchedule);
            instructionText = itemView.findViewById(R.id.instructionText);
            medTakenTime = itemView.findViewById(R.id.medTakenTime);
            openTimePickerTextView = itemView.findViewById(R.id.timeMedSchedule);
        }
    }

    public interface OnReminderItemListener {
        void onOpenTextClick(Reminder rem);
    }
}
