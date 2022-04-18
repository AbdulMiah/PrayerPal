package com.example.mob_dev_portfolio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.databases.PrayerDB;
import com.example.mob_dev_portfolio.fragments.DuaApiFragment;
import com.example.mob_dev_portfolio.fragments.PrayerTimesFragment;
import com.example.mob_dev_portfolio.fragments.QiblaFragment;
import com.example.mob_dev_portfolio.fragments.TrackerFragment;
import com.example.mob_dev_portfolio.models.PrayerModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private PrayerDB db;
    private BottomNavigationView bottomNavView;
    private Map<String, Long> notificationTimes = new HashMap<>();
    private ArrayList<Long> test = new ArrayList<>();

    public static final String FAJR_CHANNEL_ID = "Fajr";
    public static final String DHUHR_CHANNEL_ID = "Dhuhr";
    public static final String ASR_CHANNEL_ID = "Asr";
    public static final String MAGHRIB_CHANNEL_ID = "Maghrib";
    public static final String ISHA_CHANNEL_ID = "Isha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context c = getBaseContext();

        createNotifChannels();

        this.db = Room.databaseBuilder(
                c,
                PrayerDB.class,
                "prayer-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        try {
            List<PrayerModel> prayers = db.prayerDAO().getAllPrayers();
            for (int i = 0; i < 6; i++) {
                if (i==1) {
                    continue;
                }
//                System.out.println(prayers.get(i).getPrayerName());

                String time = prayers.get(i).getPrayerTime();
                LocalDate now = LocalDate.now();
                String comb = now.toString()+" "+time+":00";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(comb);
                long millis = date.getTime();
//                System.out.println(millis);

                notificationTimes.put(prayers.get(i).getPrayerName(), millis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(notificationTimes);

//        test.add(1650209700000L);
//        test.add(1650209760000L);
//        test.add(1650209820000L);
//        test.add(1650209880000L);
//        for (long m : test) {
//            System.out.println(m);
//            scheduleNotif(c, m, (int) m,"testing this", "hello world"+m);
//        }

//        for (int i = 0; i < notificationTimes.size(); i++) {
//            System.out.println(notificationTimes.);
//        }

        int x = 0;
        for (Map.Entry<String, Long> entry: notificationTimes.entrySet()) {
            Date d = new Date(entry.getValue());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String date = sdf.format(d);
            scheduleNotif(c, entry.getValue(), x, entry.getKey(), entry.getKey()+" is at "+date, "Hurry up! It's time to pray "+entry.getKey()+"!");
            x += 1;
        }

//        scheduleNotif(c, 1650296460000L, 0, FAJR_CHANNEL_ID, "test notif", "hello notificationers for 04:41");
//        scheduleNotif(c, 1650296520000L, 1, DHUHR_CHANNEL_ID,"test notif", "hello notificationers for 04:42");
//        scheduleNotif(c, 1650296580000L, 2, ASR_CHANNEL_ID, "test notif", "hello notificationers for 04:43");
//        scheduleNotif(c, 1650296640000L, 3, MAGHRIB_CHANNEL_ID, "test notif", "hello notificationers for 04:44");

        // Load PrayerTimesFragment on application startup
        changeInternalFragment(new PrayerTimesFragment(), R.id.main_frag_container);

        // Add an item listener to the bottom navigation view
        bottomNavView = findViewById(R.id.bottom_nav);
        bottomNavView.setOnNavigationItemSelectedListener(this);
    }

    private void scheduleNotif(Context c, long time, int notifID, String channelID, String title, String msg) {
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

    private void createNotifChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Fajr Channel
            NotificationChannel fajrChannel = new NotificationChannel(
                    FAJR_CHANNEL_ID,
                    "Fajr Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            fajrChannel.setDescription("Description of Fajr Channel");

            // Dhuhr Channel
            NotificationChannel dhuhrChannel = new NotificationChannel(
                    DHUHR_CHANNEL_ID,
                    "Dhuhr Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            dhuhrChannel.setDescription("Description of Dhuhr Channel");

            // Asr Channel
            NotificationChannel asrChannel = new NotificationChannel(
                    ASR_CHANNEL_ID,
                    "Asr Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            asrChannel.setDescription("Description of Asr Channel");

            // Maghrib Channel
            NotificationChannel maghribChannel = new NotificationChannel(
                    MAGHRIB_CHANNEL_ID,
                    "Maghrib Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            maghribChannel.setDescription("Description of Maghrib Channel");

            // Isha Channel
            NotificationChannel ishaChannel = new NotificationChannel(
                    ISHA_CHANNEL_ID,
                    "Isha Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            ishaChannel.setDescription("Description of Isha Channel");

            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.createNotificationChannel(fajrChannel);
            notifManager.createNotificationChannel(dhuhrChannel);
            notifManager.createNotificationChannel(asrChannel);
            notifManager.createNotificationChannel(maghribChannel);
            notifManager.createNotificationChannel(ishaChannel);
        }
    }

    // Method to replace internal fragment
    private void changeInternalFragment(Fragment fragment, int fragmentContainer){
        FragmentManager supportFragmentManager = getSupportFragmentManager();

        supportFragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_prayer_times:
                changeInternalFragment(new PrayerTimesFragment(), R.id.main_frag_container);
                break;
            case R.id.nav_qibla:
                changeInternalFragment(new QiblaFragment(), R.id.main_frag_container);
                break;
            case R.id.nav_tracker:
                changeInternalFragment(new TrackerFragment(), R.id.main_frag_container);
                break;
            case R.id.nav_dua:
                changeInternalFragment(new DuaApiFragment(), R.id.main_frag_container);
                break;
            default:
                changeInternalFragment(new PrayerTimesFragment(), R.id.main_frag_container);
                break;
        }
        return true;
    }

}