package com.example.mob_dev_portfolio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TrackerFragment extends Fragment {

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

        calendarView = v.findViewById(R.id.calendar);
        trackerDate = v.findViewById(R.id.tracker_date);

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

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String date = day+"-"+(month+1)+"-"+year;
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