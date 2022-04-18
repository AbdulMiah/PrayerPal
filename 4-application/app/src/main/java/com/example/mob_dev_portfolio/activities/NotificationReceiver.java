package com.example.mob_dev_portfolio.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.mob_dev_portfolio.R;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String FAJR_NOTIF_ID = "Fajr";
//    private int notifID = 1;
    public String titleExtra = "title";
    public String msgExtra = "message";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppIntent = PendingIntent.getActivity(context, 10, appIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notif = new NotificationCompat.Builder(context, intent.getStringExtra("channelID"))
                .setSmallIcon(R.drawable.ic_hands_praying_notif)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(msgExtra))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .addAction(0, "Open App", openAppIntent)
                .setContentIntent(openAppIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(intent.getIntExtra("notifID", 2), notif);
    }
}
