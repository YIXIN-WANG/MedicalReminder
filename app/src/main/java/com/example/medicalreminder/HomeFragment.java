package com.example.medicalreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.github.techisfun.onelinecalendar.DateSelectionListener;
import com.github.techisfun.onelinecalendar.OneLineCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
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
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("Home");

        sendNotification("blal");

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
//        picker=(TimePicker)frag.findViewById(R.id.timePicker1);
//        picker.setIs24HourView(true);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext(), channelId)
                        .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                        .setContentTitle("Smart-box Notification")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCom = NotificationManagerCompat.from(getContext());
        notificationManagerCom.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}

