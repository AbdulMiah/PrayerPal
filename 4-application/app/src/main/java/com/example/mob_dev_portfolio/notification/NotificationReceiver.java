package com.example.mob_dev_portfolio.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NotificationReceiver", "Broadcast is running...");

        // Create an intent for the Notification Service and pass the relevant data
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra(NotificationService.notificationIDExtra, intent.getIntExtra(NotificationService.notificationIDExtra, 0));
        i.putExtra(NotificationService.channelIDExtra, intent.getStringExtra(NotificationService.channelIDExtra));
        i.putExtra(NotificationService.titleExtra, intent.getStringExtra(NotificationService.titleExtra));
        i.putExtra(NotificationService.msgExtra, intent.getStringExtra(NotificationService.msgExtra));

        // Start the Service
        context.startService(i);
    }
}
