package com.example.medicalreminder;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.medicalreminder.adapter.HomeItemListAdapter;
import com.example.medicalreminder.model.Reminder;
import com.example.medicalreminder.service.MyFirebaseMessagingService;
import com.example.medicalreminder.service.NotificationServices;
import com.github.techisfun.onelinecalendar.DateSelectionListener;
import com.github.techisfun.onelinecalendar.OneLineCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.time.Instant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Context mContext;
    private NotificationServices ns;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(MyFirebaseMessagingService.INFO_UPDATE)){
                    //Reminder reminder = (Reminder)intent.getSerializableExtra(MyFirebaseMessagingService.INFO_UPDATE);
                    //long index = reminder.getSequence()-1;
                    //remList.set(Math.toIntExact(index), reminder);
                    homeItemListAdapter.notifyDataSetChanged();
                }
            }
        }, new IntentFilter(MyFirebaseMessagingService.INFO_UPDATE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_home, container, false);
        this.mContext = getActivity();
        //this.ns = new NotificationServices(mContext);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("Home");

        //ns.scheduleNotification(ns.getNotification("test"), 5000);
        OneLineCalendarView calendarView = (OneLineCalendarView) fragView.findViewById(R.id.calendar_view);
        recyclerView = fragView.findViewById(R.id.home_recycleView);

        recyclerView.setHasFixedSize(true);
        homeItemListAdapter = new HomeItemListAdapter(getActivity(), remList, this);
        recyclerView.setAdapter(homeItemListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbRemRef = FirebaseDatabase.getInstance().getReference("Reminder");

        calendarView.setOnDateClickListener(new DateSelectionListener() {
            @Override
            public boolean onDateSelected(@NonNull Date date) {
                long startOfSelectDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endOfSelectDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                calendarView.setStickyHeaderText(SimpleDateFormat.getDateInstance().format(date));
                Query query  = dbRemRef.orderByChild("scheduleTime").startAt(startOfSelectDate).endAt(endOfSelectDate);
                query.addValueEventListener(new ValueEventListener() {
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

    private long modifyTime(long time, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onOpenTextClick(Reminder rem) {
        if (!rem.isTakenMed()){
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getActivity(), (timePicker, selectedHour, selectedMinute) -> {
                rem.setScheduleTime(modifyTime(rem.getScheduleTime(),selectedHour,selectedMinute));
                setReminderValue(rem);
                UpdateReminderTime(rem);
                homeItemListAdapter.notifyDataSetChanged();
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }

    private void UpdateReminderTime(Reminder rem){
        long startOfSelectDate = Instant.ofEpochMilli(rem.getScheduleTime()).atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfSelectDate = Instant.ofEpochMilli(rem.getScheduleTime()).atZone(ZoneId.systemDefault())
                .toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(); //restricted to 1 day frame
        Query query  = dbRemRef.orderByChild("scheduleTime").startAt(startOfSelectDate).endAt(endOfSelectDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Reminder> reminders = new ArrayList<>();
                for(DataSnapshot clinicSnapshot : dataSnapshot.getChildren()){
                    Reminder rem = clinicSnapshot.getValue(Reminder.class);
                    if(!rem.isTakenMed()){
                        reminders.add(rem);
                    }
                }
                if(reminders.size() > 1){
                    for (int i = 0; i < reminders.size(); i++){
                        Reminder tmprem = reminders.get(i);
                        if (tmprem.getReminderId() != rem.getReminderId()){
                            LocalDateTime newScheduleTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(rem.getScheduleTime()),
                                    ZoneId.systemDefault()).plusHours(6);
                            tmprem.setScheduleTime(ZonedDateTime.of(newScheduleTime, ZoneId.systemDefault()).toInstant().toEpochMilli());
                            setReminderValue(tmprem);
                        }
                    }
                    query.removeEventListener(this);
                    //homeItemListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Retrieve Clinics Error:",databaseError.getMessage());
            }
        });
    }

    private void setReminderValue(Reminder reminder){
        dbRemRef.child(reminder.getReminderId())
                .setValue(reminder).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("Taken med ", "Success");
                        Intent i = new Intent("INFO_UPDATE");
                        i.putExtra("INFO_UPDATE", reminder);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);
                    }
                    else{
                        Log.e("Taken med ",task.getException().getMessage());
                    }
                });
    }

}

