package com.example.mob_dev_portfolio.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationHelper {

    public static final String PRAYER_GROUP_ID = "prayerChannel";
    public static final String FAJR_CHANNEL_ID = "Fajr";
    public static final String DHUHR_CHANNEL_ID = "Dhuhr";
    public static final String ASR_CHANNEL_ID = "Asr";
    public static final String MAGHRIB_CHANNEL_ID = "Maghrib";
    public static final String ISHA_CHANNEL_ID = "Isha";

    public static void createNotificationChannels(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannelGroup group = new NotificationChannelGroup(
                    PRAYER_GROUP_ID,
                    "Prayer"
            );

            // Fajr Channel
            NotificationChannel fajrChannel = new NotificationChannel(
                    FAJR_CHANNEL_ID,
                    "Fajr Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            fajrChannel.setDescription("This Channel Is For The Fajr Notification");
            fajrChannel.setGroup(PRAYER_GROUP_ID);

            // Dhuhr Channel
            NotificationChannel dhuhrChannel = new NotificationChannel(
                    DHUHR_CHANNEL_ID,
                    "Dhuhr Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            dhuhrChannel.setDescription("This Channel Is For The Dhuhr Notification");
            dhuhrChannel.setGroup(PRAYER_GROUP_ID);

            // Asr Channel
            NotificationChannel asrChannel = new NotificationChannel(
                    ASR_CHANNEL_ID,
                    "Asr Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            asrChannel.setDescription("This Channel Is For The Asr Notification");
            asrChannel.setGroup(PRAYER_GROUP_ID);

            // Maghrib Channel
            NotificationChannel maghribChannel = new NotificationChannel(
                    MAGHRIB_CHANNEL_ID,
                    "Maghrib Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            maghribChannel.setDescription("This Channel Is For The Maghrib Notification");
            maghribChannel.setGroup(PRAYER_GROUP_ID);

            // Isha Channel
            NotificationChannel ishaChannel = new NotificationChannel(
                    ISHA_CHANNEL_ID,
                    "Isha Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            ishaChannel.setDescription("This Channel Is For The Isha Notification");
            ishaChannel.setGroup(PRAYER_GROUP_ID);

            NotificationManager notifManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.createNotificationChannelGroup(group);
            notifManager.createNotificationChannel(fajrChannel);
            notifManager.createNotificationChannel(dhuhrChannel);
            notifManager.createNotificationChannel(asrChannel);
            notifManager.createNotificationChannel(maghribChannel);
            notifManager.createNotificationChannel(ishaChannel);
        }
    }

    public static void scheduleNotification(Context c, long time, int notifID, String channelID, String title, String msg) {
        Intent intent = new Intent(c, NotificationReceiver.class);
        intent.putExtra("notifID", notifID);
        intent.putExtra("channelID", channelID);
        intent.putExtra("title", title);
        intent.putExtra("message", msg);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                c,
                notifID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarm = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarm.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
        );
    }
}
