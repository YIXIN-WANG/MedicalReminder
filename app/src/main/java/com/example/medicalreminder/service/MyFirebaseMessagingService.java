package com.example.medicalreminder.service;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.medicalreminder.HomeActivity;
import com.example.medicalreminder.ProfileActivity;
import com.example.medicalreminder.R;
import com.example.medicalreminder.model.Reminder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String INFO_UPDATE = "info_update";
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//
//        }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            UpdateReminderTime();
            sendNotification(remoteMessage.getNotification().getBody());
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void UpdateReminderTime(){
        long startOfSelectDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfSelectDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Query query  = FirebaseDatabase.getInstance().getReference("Reminder").orderByChild("scheduleTime").startAt(startOfSelectDate).endAt(endOfSelectDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Reminder> reminders = new ArrayList<>();
                for(DataSnapshot clinicSnapshot : dataSnapshot.getChildren()){
                    Reminder rem = clinicSnapshot.getValue(Reminder.class);
                    if(!rem.isTakenMed() && rem.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        reminders.add(rem);
                    }
                }
                if(reminders.size() > 0){
                    Reminder reminder = reminders.remove(0);
                    reminder.setTakeTime(System.currentTimeMillis());
                    reminder.setTakenMed(true);

                    if(reminders.size() > 0){
                        Reminder nextReminder = reminders.get(0);
                        LocalDateTime newScheduleTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminder.getTakeTime()), ZoneId.systemDefault()).plusHours(6);
                        nextReminder.setScheduleTime(ZonedDateTime.of(newScheduleTime, ZoneId.systemDefault()).toInstant().toEpochMilli());
                        setReminderValue(nextReminder);
                    }
                    setReminderValue(reminder);
                    query.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Retrieve Clinics Error:",databaseError.getMessage());
            }
        });
    }

    private void setReminderValue(Reminder reminder){
        FirebaseDatabase.getInstance().getReference("Reminder").child(reminder.getReminderId())
                .setValue(reminder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Taken med ", "Success");
                    Intent i = new Intent(INFO_UPDATE);
                    i.putExtra(INFO_UPDATE, reminder);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }
                else{
                    Log.e("Taken med ",task.getException().getMessage());
                }
            }
        });
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                        .setContentTitle("Smart-box Notification")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCom = NotificationManagerCompat.from(this);
        notificationManagerCom.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
