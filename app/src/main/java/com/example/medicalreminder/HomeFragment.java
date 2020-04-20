package com.example.medicalreminder;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.CalendarView;
import android.widget.TimePicker;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
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
    private TimePicker picker;
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
        this.ns = new NotificationServices(mContext);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("Home");

        ns.scheduleNotification(ns.getNotification("test"), 5000);
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

    @Override
    public void onOpenTextClick(Reminder rem) {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Date date = new Date(rem.getScheduleTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.MINUTE, selectedMinute);
                calendar.set(Calendar.HOUR, selectedHour);
                rem.setScheduleTime(calendar.getTimeInMillis());
                homeItemListAdapter.notifyDataSetChanged();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


}

