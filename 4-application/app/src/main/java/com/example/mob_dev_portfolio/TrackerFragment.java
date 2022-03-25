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

        // Instantiating Views
        calendarView = v.findViewById(R.id.calendar);
        trackerDate = v.findViewById(R.id.tracker_date);

        // Instantiating Checkbox Views
        this.fajrCheckbox = v.findViewById(R.id.fajr_checkbox);
        this.dhuhrCheckbox = v.findViewById(R.id.dhuhr_checkbox);
        this.asrCheckbox = v.findViewById(R.id.asr_checkbox);
        this.maghribCheckbox = v.findViewById(R.id.maghrib_checkbox);
        this.ishaCheckbox = v.findViewById(R.id.isha_checkbox);

        // Adding checkboxes to an ArrayList
        checkBoxList.add(fajrCheckbox);
        checkBoxList.add(dhuhrCheckbox);
        checkBoxList.add(asrCheckbox);
        checkBoxList.add(maghribCheckbox);
        checkBoxList.add(ishaCheckbox);
//        Log.d("List of Checkboxes", checkBoxList.toString());

        // Build the database
        TrackerDB db = Room.databaseBuilder(
                getContext(),
                TrackerDB.class,
                "tracker-database").build();
        this.executor = Executors.newFixedThreadPool(4);

        // Some test tracker data
        Tracker t1 = new Tracker(1, "1-3-2022", true, false, false, true, true);
        Tracker t2 = new Tracker(2, "2-3-2022", false, false, true, true, true);
        Tracker t3 = new Tracker(3, "3-3-2022", true, true, true, false, false);
        Tracker t4 = new Tracker(4, "4-3-2022", true, true, true, true, true);
        Tracker t5 = new Tracker(5, "5-3-2022", true, false, true, false, true);

        // Insert test data into DB
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.trackerDAO().insertTracker(t1);
                db.trackerDAO().insertTracker(t2);
                db.trackerDAO().insertTracker(t3);
                db.trackerDAO().insertTracker(t4);
                db.trackerDAO().insertTracker(t5);

            }
        });

        // Set tracker for today
        // Get the current date and format it, then set the tracker date to today's date by default
        Tracker newTracker = new Tracker();
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, d MMMM yyyy");
        SimpleDateFormat calendarFormat = new SimpleDateFormat("d-M-yyyy");
        Date now = new Date();
        String formatDate = sdfDate.format(now);
        String dateToday = calendarFormat.format(now);
        trackerDate.setText(formatDate);
        newTracker.setDate(dateToday);

        // Iterate through the checkboxes and add an onClick listener to them
        for (int i = 0; i < checkBoxList.size(); i++) {
            checkBoxList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // If the button is checked, find prayer that is checked and set the prayer status
                    if (((CompoundButton) view).isChecked()) {
                        if (((CompoundButton) view).getText().equals("Fajr")) {
                            newTracker.setFajrPrayed(true);
                        }
                        if (((CompoundButton) view).getText().equals("Dhuhr")) {
                            newTracker.setDhuhrPrayed(true);
                        }
                        if (((CompoundButton) view).getText().equals("Asr")) {
                            newTracker.setAsrPrayed(true);
                        }
                        if (((CompoundButton) view).getText().equals("Maghrib")) {
                            newTracker.setMaghribPrayed(true);
                        }
                        if (((CompoundButton) view).getText().equals("Isha")) {
                            newTracker.setIshaPrayed(true);
                        }
                    } else {
                        if (((CompoundButton) view).getText().equals("Fajr")) {
                            newTracker.setFajrPrayed(false);
                        }
                        if (((CompoundButton) view).getText().equals("Dhuhr")) {
                            newTracker.setDhuhrPrayed(false);
                        }
                        if (((CompoundButton) view).getText().equals("Asr")) {
                            newTracker.setAsrPrayed(false);
                        }
                        if (((CompoundButton) view).getText().equals("Maghrib")) {
                            newTracker.setMaghribPrayed(false);
                        }
                        if (((CompoundButton) view).getText().equals("Isha")) {
                            newTracker.setIshaPrayed(false);
                        }
                    }

                    // Check for any null values before inserting into the database
                    if (newTracker.getFajrPrayed() == null) {
                        newTracker.setFajrPrayed(false);
                    }
                    if (newTracker.getDhuhrPrayed() == null) {
                        newTracker.setDhuhrPrayed(false);
                    }
                    if (newTracker.getAsrPrayed() == null) {
                        newTracker.setAsrPrayed(false);
                    }
                    if (newTracker.getMaghribPrayed() == null) {
                        newTracker.setMaghribPrayed(false);
                    }
                    if (newTracker.getIshaPrayed() == null) {
                        newTracker.setIshaPrayed(false);
                    }

                    // Inserting the tracker into the DB
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            // Since I am entering records into the database using
                            // onClick, I delete the prior record with the same date
                            // and just insert the most recent record.
                            if (db.trackerDAO().getTrackerByDate(dateToday) != null) {
                                db.trackerDAO().deleteTrackerByDate(dateToday);
                                db.trackerDAO().insertTracker(newTracker);
                            } else {
                                db.trackerDAO().insertTracker(newTracker);
                            }
                        }
                    });
                }
            });
        }

        // Set a dateChangeListener, and set the tracker date to the date selected from the calendar
        // Set onClickListener to the checkboxes
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
             @Override
             public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                 String date = day + "-" + (month + 1) + "-" + year;
                 Log.i("DATE", date);
                 setTrackerDate(date);

                 // Clear all the checkboxes on calendar date change
                 fajrCheckbox.setChecked(false);
                 dhuhrCheckbox.setChecked(false);
                 asrCheckbox.setChecked(false);
                 maghribCheckbox.setChecked(false);
                 ishaCheckbox.setChecked(false);

                 executor.execute(new Runnable() {
                     @Override
                     public void run() {
                         try {
                             // Delete the first record when the number of records reaches 31
                             List<Tracker> trackerList = db.trackerDAO().getAllTracker();
                             if (trackerList.size() >= 31) {
                                 db.trackerDAO().deleteTracker(trackerList.get(0));
                             }

                             // Get the tracker by date selected on calendar
                             Tracker tr = db.trackerDAO().getTrackerByDate(date);
                             System.out.println(tr);

                             // If tracker exists for this date, show tracker
                             if (tr != null) {
                                 fajrCheckbox.setChecked(tr.getFajrPrayed());
                                 dhuhrCheckbox.setChecked(tr.getDhuhrPrayed());
                                 asrCheckbox.setChecked(tr.getAsrPrayed());
                                 maghribCheckbox.setChecked(tr.getMaghribPrayed());
                                 ishaCheckbox.setChecked(tr.getIshaPrayed());
                             } else { //if (tr == null)
                                 // https://stackoverflow.com/questions/22564113/oncheckedchangelistener-or-onclicklistener-with-if-statement-for-checkboxs-wha
                                 // https://stackoverflow.com/questions/8386832/android-checkbox-listener
                                 Tracker newTracker = new Tracker();
                                 newTracker.setDate(date);

                                 // Iterate through the checkboxes and add an onClick listener to them
                                 for (int i = 0; i < checkBoxList.size(); i++) {
                                     checkBoxList.get(i).setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             // If the button is checked, find prayer that is checked and set the prayer status
                                             if (((CompoundButton) view).isChecked()) {
                                                 if (((CompoundButton) view).getText().equals("Fajr")) {
                                                     newTracker.setFajrPrayed(true);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Dhuhr")) {
                                                     newTracker.setDhuhrPrayed(true);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Asr")) {
                                                     newTracker.setAsrPrayed(true);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Maghrib")) {
                                                     newTracker.setMaghribPrayed(true);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Isha")) {
                                                     newTracker.setIshaPrayed(true);
                                                 }
                                             } else {
                                                 if (((CompoundButton) view).getText().equals("Fajr")) {
                                                     newTracker.setFajrPrayed(false);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Dhuhr")) {
                                                     newTracker.setDhuhrPrayed(false);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Asr")) {
                                                     newTracker.setAsrPrayed(false);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Maghrib")) {
                                                     newTracker.setMaghribPrayed(false);
                                                 }
                                                 if (((CompoundButton) view).getText().equals("Isha")) {
                                                     newTracker.setIshaPrayed(false);
                                                 }
                                             }

                                             // Check for any null values before inserting into the database
                                             if (newTracker.getFajrPrayed() == null) {
                                                 newTracker.setFajrPrayed(false);
                                             }
                                             if (newTracker.getDhuhrPrayed() == null) {
                                                 newTracker.setDhuhrPrayed(false);
                                             }
                                             if (newTracker.getAsrPrayed() == null) {
                                                 newTracker.setAsrPrayed(false);
                                             }
                                             if (newTracker.getMaghribPrayed() == null) {
                                                 newTracker.setMaghribPrayed(false);
                                             }
                                             if (newTracker.getIshaPrayed() == null) {
                                                 newTracker.setIshaPrayed(false);
                                             }

                                             // Inserting the tracker into the DB
                                             executor.execute(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     // When I enter a record into the database using onClick,
                                                     // I delete the prior record with the same date and just
                                                     // insert the most recent record.
                                                     if (db.trackerDAO().getTrackerByDate(date) != null) {
                                                         db.trackerDAO().deleteTrackerByDate(date);
                                                         db.trackerDAO().insertTracker(newTracker);
                                                     } else {
                                                         db.trackerDAO().insertTracker(newTracker);
                                                     }
                                                 }
                                             });
                                         }
                                     });
                                 }
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                 });
             }
         });

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