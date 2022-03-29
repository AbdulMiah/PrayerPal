package com.example.mob_dev_portfolio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.fragments.DuaApiFragment;
import com.example.mob_dev_portfolio.fragments.PrayerTimesFragment;
import com.example.mob_dev_portfolio.fragments.QiblaFragment;
import com.example.mob_dev_portfolio.fragments.TrackerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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