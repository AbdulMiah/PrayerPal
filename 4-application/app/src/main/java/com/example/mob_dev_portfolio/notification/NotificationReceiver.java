package com.example.mob_dev_portfolio.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NotificationReceiver", "Broadcast is running...");

        Intent i = new Intent(context, NotificationService.class);
        i.putExtra("notifID", intent.getIntExtra("notifID", 0));
        i.putExtra("channelID", intent.getStringExtra("channelID"));
        i.putExtra("title", intent.getStringExtra("title"));
        i.putExtra("message", intent.getStringExtra("message"));

        context.startService(i);
    }
}
