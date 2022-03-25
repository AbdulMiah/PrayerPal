package com.example.mob_dev_portfolio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mob_dev_portfolio.databases.TrackerDB;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackerFragment extends Fragment {

    ExecutorService executor;

    private CalendarView calendarView;
    private TextView trackerDate;
    private CheckBox fajrCheckbox;
    private CheckBox dhuhrCheckbox;
    private CheckBox asrCheckbox;
    private CheckBox maghribCheckbox;
    private CheckBox ishaCheckbox;
    private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    public TrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tracker, container, false);

        TrackerDB db = Room.databaseBuilder(
                getContext(),
                TrackerDB.class,
                "tracker-database").build();
        this.executor = Executors.newFixedThreadPool(4);

        calendarView = v.findViewById(R.id.calendar);
        trackerDate = v.findViewById(R.id.tracker_date);

        // Get the current date and format it, then set the tracker date to today's date by default
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, d MMMM yyyy");
        Date now = new Date();
        String formatDate = sdfDate.format(now);
        trackerDate.setText(formatDate);

        this.fajrCheckbox = v.findViewById(R.id.fajr_checkbox);
        this.dhuhrCheckbox = v.findViewById(R.id.dhuhr_checkbox);
        this.asrCheckbox = v.findViewById(R.id.asr_checkbox);
        this.maghribCheckbox = v.findViewById(R.id.maghrib_checkbox);
        this.ishaCheckbox = v.findViewById(R.id.isha_checkbox);

        checkBoxList.add(fajrCheckbox);
        checkBoxList.add(dhuhrCheckbox);
        checkBoxList.add(asrCheckbox);
        checkBoxList.add(maghribCheckbox);
        checkBoxList.add(ishaCheckbox);
//        Log.d("List of Checkboxes", checkBoxList.toString());

        // Set a dateChangeListener, set the tracker date to the date selected from the calendar
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String date = day + "-" + (month + 1) + "-" + year;
                setTrackerDate(date);
            }
        });

        for (int i=0; i<checkBoxList.size(); i++) {
            checkBoxList.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b == true) {
                        Toast.makeText(v.getContext(), "You checked "+compoundButton.getText(), Toast.LENGTH_SHORT).show();
                    } else if (b == false) {
                        Toast.makeText(v.getContext(), "You unchecked "+compoundButton.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return v;
    }

    public void setTrackerDate(String calendarDate) {
        // Format date from calendar, then set the tracker date text to formatted date
        DateTimeFormatter trackerFormat = DateTimeFormatter.ofPattern("d-M-yyyy");
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        LocalDate formatDate = LocalDate.parse(calendarDate, trackerFormat);
        String output = myFormat.format(formatDate);
        trackerDate.setText(output);
    }
}