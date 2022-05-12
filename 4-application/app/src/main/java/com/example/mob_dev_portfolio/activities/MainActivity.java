package com.example.mob_dev_portfolio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.databases.PrayerDB;
import com.example.mob_dev_portfolio.fragments.DuaApiFragment;
import com.example.mob_dev_portfolio.fragments.PrayerTimesFragment;
import com.example.mob_dev_portfolio.fragments.QiblaFragment;
import com.example.mob_dev_portfolio.fragments.TrackerFragment;
import com.example.mob_dev_portfolio.models.PrayerModel;
import com.example.mob_dev_portfolio.notification.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private PrayerDB db;
    private BottomNavigationView bottomNavView;
    private Map<String, Long> notificationTimes = new HashMap<>();
    private ArrayList<Long> test = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context c = getBaseContext();

        // Create the Notification channels
        NotificationHelper.createNotificationChannels(c);

        this.db = Room.databaseBuilder(
                c,
                PrayerDB.class,
                "prayer-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // Get the list of all prayers and their times
        try {
            List<PrayerModel> prayers = db.prayerDAO().getAllPrayers();
            for (int i = 0; i < 6; i++) {
                if (i==1) {
                    continue;
                }
                // Get the prayer time
                String time = prayers.get(i).getPrayerTime();

                // Convert time into Unix timestamp
                LocalDate now = LocalDate.now();
                String comb = now.toString()+" "+time+":00";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(comb);
                long millis = date.getTime();

                // Add the prayer times in millis to a HashMap to use for notifications
                notificationTimes.put(prayers.get(i).getPrayerName(), millis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setNotificationScheduler(c);

        // Load PrayerTimesFragment on application startup
        changeInternalFragment(new PrayerTimesFragment(), R.id.main_frag_container);

        // Add an item listener to the bottom navigation view
        bottomNavView = findViewById(R.id.bottom_nav);
        bottomNavView.setOnNavigationItemSelectedListener(this);
    }

    // Method to replace internal fragment
    private void changeInternalFragment(Fragment fragment, int fragmentContainer){
        FragmentManager supportFragmentManager = getSupportFragmentManager();

        supportFragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();
    }

    public void setNotificationScheduler(Context c) {
        // Iterate through the notification HashMap
        int x = 0;
        for (Map.Entry<String, Long> entry: notificationTimes.entrySet()) {
            // Format the date to use in the notification title
            Date d = new Date(entry.getValue());
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            String date = sdf.format(d);

            // If the prayer time is after or equal to now, then schedule the notification
            if (entry.getValue()>=System.currentTimeMillis()) {
                NotificationHelper.scheduleNotification(c, entry.getValue(), x, entry.getKey(), entry.getKey()+" is at "+date, "Hurry up! It's time to pray "+entry.getKey()+"!");
            }
            x += 1;
        }
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