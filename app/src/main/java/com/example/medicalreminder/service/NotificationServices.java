package com.example.medicalreminder.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.SystemClock;

import androidx.core.app.NotificationCompat;


import com.example.medicalreminder.NotificationPublisher;
import com.example.medicalreminder.R;

public class NotificationServices {

    private Context mContext;

    public NotificationServices(Context context){
        this.mContext = context;
    }

    public void scheduleNotification (Notification notification, long delay) {
        Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID,1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast (mContext,0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,futureInMillis,pendingIntent);
    }

    public Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationPublisher.NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle("Medication");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_add_alert_black_24dp);
        builder.setAutoCancel(true);
        builder.setChannelId(NotificationPublisher.NOTIFICATION_CHANNEL_ID);//NotificationPublisher.NOTIFICATION_CHANNEL_ID
        return builder.build();
    }

    public void cancelNotification(int notificationId){
        NotificationManager nMgr = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(notificationId);
    }
}
