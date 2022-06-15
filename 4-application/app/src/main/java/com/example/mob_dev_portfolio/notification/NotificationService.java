package com.example.mob_dev_portfolio.notification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.databases.PrayerDB;
import com.example.mob_dev_portfolio.models.PrayerModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Adapted from https://guides.codepath.com/android/Starting-Background-Services#using-with-alarmmanager-for-periodic-tasks
// Used Service instead of IntentService shown in tutorial
public class NotificationService extends Service {

    private PrayerDB db;
    ExecutorService executor;

    private String[] prayerNamesList;
    private ArrayList<PrayerModel> prayerModels = new ArrayList<>();
    private Map<String, Long> notificationTimes = new HashMap<>();

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public NotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.i("NotificationService", "Service is running...");

        sp = getApplicationContext().getSharedPreferences("locationData", Context.MODE_PRIVATE);
        this.editor = sp.edit();
        prayerNamesList = getApplicationContext().getResources().getStringArray(R.array.prayer_names);

        // Build the database
        this.db = Room.databaseBuilder(
                getApplicationContext(),
                PrayerDB.class,
                "prayer-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        this.executor = Executors.newFixedThreadPool(4);

//        onAPIRequest(sp.getFloat("latitude", 0f), sp.getFloat("longitude", 0f));

        this.prayerModels = (ArrayList<PrayerModel>) db.prayerDAO().getAllPrayers();

        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        LocalDate now = LocalDate.now();
        String dateNow = myFormat.format(now);

        String storedDate = sp.getString("date", "");

        // If prayer list is not empty and the date is not equal to today's date, then request the data from API and schedule notifications
        if ((!prayerModels.isEmpty()) && (!storedDate.equals(dateNow))) {
            System.out.println("Oops, getting it!");
            callAPIAndScheduleNotifications();
        } else {
            System.out.println("Got it!");
            System.out.println(prayerModels.toString());
            getPrayerTimes();
            scheduleNotifications();
        }
        return START_STICKY;
    }

    private void callAPIAndScheduleNotifications() {
        float lat = sp.getFloat("latitude", 0f);
        float lng = sp.getFloat("longitude", 0f);
        onAPIRequest(getApplicationContext(), lat, lng);

        System.out.println(prayerModels.toString());
        getPrayerTimes();
        scheduleNotifications();
    }

    private void getPrayerTimes() {
        // Get the list of all prayers and their times
        try {
            List<PrayerModel> prayers = db.prayerDAO().getAllPrayers();
            for (int i = 0; i < 6; i++) {
                if (i == 1) {
                    continue;
                }
                // Get the prayer time
                String time = prayers.get(i).getPrayerTime();

                // Convert time into Unix timestamp
                LocalDate now = LocalDate.now();
                String comb = now.toString() + " " + time + ":00";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(comb);
                long millis = date.getTime();

                // Add the prayer times in millis to a HashMap to use for notifications
                notificationTimes.put(prayers.get(i).getPrayerName(), millis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotifications() {
        // Iterate through the notification HashMap
        int x = 0;
        for (Map.Entry<String, Long> entry : notificationTimes.entrySet()) {
            // Format the date to use in the notification title
            Date d = new Date(entry.getValue());
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            String date = sdf.format(d);

            // If the prayer time is after or equal to now, then schedule the notification
            if (entry.getValue() >= System.currentTimeMillis()) {
                NotificationHelper.scheduleNotification(getApplicationContext(), entry.getValue(), x, entry.getKey(), entry.getKey()+" is at "+date, "Hurry up! It's time to pray "+entry.getKey()+"!");
            }
            x += 1;
        }
        System.out.println("Scheduled all the Notifications!");
    }

    private void onAPIRequest(Context context, double latitude, double longitude) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate now = LocalDate.now();
        String today = dtf.format(now);

        // API URL
        String apiUrl = "https://api.aladhan.com/v1/timings/" + today + "?latitude=" + latitude + "&longitude=" + longitude + "&method=15";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Set city and country text in button retrieved from location
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geo.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            String city = addresses.get(0).getSubAdminArea();
            String country = addresses.get(0).getCountryName();

            // Setting current location text in button to prayer location from API
            String currentLocation = city.concat(", " + country);
            editor.putString("location", currentLocation);
            editor.putFloat("latitude", (float) latitude);
            editor.putFloat("longitude", (float) longitude);
            editor.commit();
            Log.d("CURRENT LOCATION ON API REQUEST", currentLocation);
        }

        // On API request, send the JSON Object response to the handleResponse() method
        JsonObjectRequest apiRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleResponse(response);
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                if(!db.prayerDAO().getAllPrayers().isEmpty()) {
                                    db.prayerDAO().clearPrayerTable();
                                }
                                for (int i = 0; i<prayerModels.size(); i++) {
                                    db.prayerDAO().insertPrayerModel(prayerModels.get(i));
                                }
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REQUEST API ERROR", "There has been an error while request data from the API");
                    }
                }
        );
        requestQueue.add(apiRequest);
    }

    private void handleResponse(JSONObject items) {
        // Check if ArrayList of PrayerModel is empty, if it is then do this...
        if (prayerModels.size() <= 0) {
            // Initialise some variables
            int listSize = prayerNamesList.length;

            try {
                // Setting JSON Objects and Arrays
                JSONObject resultsJson = items.getJSONObject("data");
                JSONObject timingsObject = resultsJson.getJSONObject("timings");

                // Looping through timings object to get prayer times and storing them in an ArrayList of PrayerModel objects
                for (int i = 0; i < listSize; i++) {
//                    Log.i(prayerNamesList[i],timingsObject.getString(prayerNamesList[i]));
                    prayerModels.add(new PrayerModel(prayerNamesList[i], timingsObject.getString(prayerNamesList[i])));
                }

                // Catch any JSONExceptions and Log to console
            } catch (JSONException e) {
                Log.e("ERROR!", e.toString());
            }
            // If ArrayList of PrayerModel is not empty, clear list and recall handleResponse() method
        } else {
            prayerModels.clear();
            handleResponse(items);
        }

        // Format current date from API, then set the current date text to formatted date
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        LocalDate now = LocalDate.now();
        String output = myFormat.format(now);
        editor.putString("date", output);
        editor.commit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
