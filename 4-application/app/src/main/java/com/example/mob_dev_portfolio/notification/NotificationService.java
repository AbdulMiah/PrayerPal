package com.example.mob_dev_portfolio.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.activities.MainActivity;

// Adapted from https://guides.codepath.com/android/Starting-Background-Services#using-with-alarmmanager-for-periodic-tasks
// Used Service instead of IntentService shown in tutorial
public class NotificationService extends Service {

    public String titleExtra = "title";
    public String msgExtra = "message";

    public NotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.i("NotificationService", "Service is running...");

        Intent appIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent openAppIntent = PendingIntent.getActivity(getApplicationContext(), 10, appIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        Notification notif = new NotificationCompat.Builder(getApplicationContext(), intent.getStringExtra("channelID"))
                .setSmallIcon(R.drawable.ic_hands_praying_notif)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(msgExtra))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .addAction(0, "Open App", openAppIntent)
                .setContentIntent(openAppIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notifManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(intent.getIntExtra("notifID", 0), notif);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
