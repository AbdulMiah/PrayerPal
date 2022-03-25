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
import com.example.mob_dev_portfolio.models.Tracker;

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

        Tracker t1 = new Tracker();
        t1.setTrackerId(1);
        t1.setDate("1-3-2022");
        t1.setFajrPrayed(true);
        t1.setDhuhrPrayed(false);
        t1.setAsrPrayed(false);
        t1.setMaghribPrayed(true);
        t1.setIshaPrayed(true);

        Tracker t2 = new Tracker();
        t2.setTrackerId(2);
        t2.setDate("2-3-2022");
        t2.setFajrPrayed(false);
        t2.setDhuhrPrayed(false);
        t2.setAsrPrayed(true);
        t2.setMaghribPrayed(true);
        t2.setIshaPrayed(true);

        Tracker t3 = new Tracker();
        t3.setTrackerId(3);
        t3.setDate("3-3-2022");
        t3.setFajrPrayed(true);
        t3.setDhuhrPrayed(true);
        t3.setAsrPrayed(true);
        t3.setMaghribPrayed(false);
        t3.setIshaPrayed(false);

        Tracker t4 = new Tracker();
        t4.setTrackerId(4);
        t4.setDate("4-3-2022");
        t4.setFajrPrayed(true);
        t4.setDhuhrPrayed(true);
        t4.setAsrPrayed(true);
        t4.setMaghribPrayed(true);
        t4.setIshaPrayed(true);

        Tracker t5 = new Tracker();
        t5.setTrackerId(5);
        t5.setDate("5-3-2022");
        t5.setFajrPrayed(true);
        t5.setDhuhrPrayed(false);
        t5.setAsrPrayed(true);
        t5.setMaghribPrayed(false);
        t5.setIshaPrayed(true);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.trackerDAO().insertTracker(t1);
                db.trackerDAO().insertTracker(t2);
                db.trackerDAO().insertTracker(t3);
                db.trackerDAO().insertTracker(t4);
                db.trackerDAO().insertTracker(t5);
//                List<Tracker> trackerList = db.trackerDAO().getAllTracker();
            }
        });

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
                Log.i("DATE", date);
                setTrackerDate(date);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Tracker tr = db.trackerDAO().getTrackerByDate(date);
                            System.out.println(tr);

                            if (tr != null) {
                                fajrCheckbox.setChecked(tr.getFajrPrayed());
                                dhuhrCheckbox.setChecked(tr.getDhuhrPrayed());
                                asrCheckbox.setChecked(tr.getAsrPrayed());
                                maghribCheckbox.setChecked(tr.getMaghribPrayed());
                                ishaCheckbox.setChecked(tr.getIshaPrayed());
                            } else {
                                fajrCheckbox.setChecked(false);
                                dhuhrCheckbox.setChecked(false);
                                asrCheckbox.setChecked(false);
                                maghribCheckbox.setChecked(false);
                                ishaCheckbox.setChecked(false);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

//        for (int i=0; i<checkBoxList.size(); i++) {
//            checkBoxList.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    if (b == true) {
//                        Toast.makeText(v.getContext(), "You checked "+compoundButton.getText(), Toast.LENGTH_SHORT).show();
//                    } else if (b == false) {
//                        Toast.makeText(v.getContext(), "You unchecked "+compoundButton.getText(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }

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