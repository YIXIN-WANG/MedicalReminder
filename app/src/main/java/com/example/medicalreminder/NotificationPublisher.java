package com.example.medicalreminder;

import android.app.AlarmManager;
import android.app.Notification ;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.Intent ;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String NOTIFICATION_CHANNEL_ID = "mCHID";
    public void onReceive (Context context , Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID ,"NOTIFICATION_CHANNEL_NAME",importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int id = intent.getIntExtra(NOTIFICATION_ID,0);
        assert notificationManager != null;
        notificationManager.notify(id,notification);
    }
}