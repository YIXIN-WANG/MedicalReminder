package com.example.medicalreminder;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.example.medicalreminder.adapter.HomeItemListAdapter;
import com.example.medicalreminder.model.Reminder;
import com.github.techisfun.onelinecalendar.DateSelectionListener;
import com.github.techisfun.onelinecalendar.OneLineCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeItemListAdapter.OnReminderItemListener {

    private List<Reminder> remList = new ArrayList<>();
    private View fragView;
    private RecyclerView recyclerView;
    private HomeItemListAdapter homeItemListAdapter;
    private DatabaseReference dbRemRef;
    private TimePicker picker;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("Home");
        recyclerView = fragView.findViewById(R.id.home_recycleView);

        recyclerView.setHasFixedSize(true);
        homeItemListAdapter = new HomeItemListAdapter(getActivity(), remList, this);
        recyclerView.setAdapter(homeItemListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbRemRef = FirebaseDatabase.getInstance().getReference("Reminder");
        dbRemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                remList.clear();
                for(DataSnapshot clinicSnapshot : dataSnapshot.getChildren()){
                    Reminder rem = clinicSnapshot.getValue(Reminder.class);
                    remList.add(rem);
                }
                homeItemListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Retrieve Clinics Error:",databaseError.getMessage());
            }
        });

        final OneLineCalendarView calendarView = (OneLineCalendarView) fragView.findViewById(R.id.calendar_view);
        calendarView.setOnDateClickListener(new DateSelectionListener() {
            @Override
            public boolean onDateSelected(@NonNull Date date) {
                calendarView.setStickyHeaderText(SimpleDateFormat.getDateInstance().format(date));
                return true;
            }

            @Override
            public boolean onDateUnselected() {
                calendarView.setStickyHeaderText("No Selection");
                return true;
            }
        });
        return fragView;
    }
    @Override
    public void onOpenTextClick(Reminder rem) {
//        picker=(TimePicker)frag.findViewById(R.id.timePicker1);
//        picker.setIs24HourView(true);
    }
}
