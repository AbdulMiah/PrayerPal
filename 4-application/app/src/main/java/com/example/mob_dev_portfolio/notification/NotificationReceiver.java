package com.example.mob_dev_portfolio.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.activities.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public static String titleExtra = "title";
    public static String msgExtra = "message";
    public static String channelIDExtra = "channelID";
    public static String notificationIDExtra = "notificationID";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NotificationReceiver", "Broadcast is running...");

        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppIntent = PendingIntent.getActivity(context, 10, appIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        Notification notif = new NotificationCompat.Builder(context, intent.getStringExtra(channelIDExtra))
                .setSmallIcon(R.drawable.ic_hands_praying_notif)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(msgExtra))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .addAction(0, "Open App", openAppIntent)
                .setContentIntent(openAppIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(intent.getIntExtra(notificationIDExtra, 0), notif);

        // Start the Service
//        Intent i = new Intent(context, NotificationService.class);
//        context.startService(i);
    }
}
