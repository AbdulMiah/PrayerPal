package com.example.mob_dev_portfolio.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.adapters.PrayerListAdapter;
import com.example.mob_dev_portfolio.activities.MapsActivity;
import com.example.mob_dev_portfolio.databases.PrayerDB;
import com.example.mob_dev_portfolio.location.LocationHelper;
import com.example.mob_dev_portfolio.location.LocationPermissions;
import com.example.mob_dev_portfolio.models.PrayerModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrayerTimesFragment extends Fragment {

    ExecutorService executor;

    // Variables for Location
    private static final int LOCATION_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private static final int LOCATION_REQUEST_FROM_BUTTON = 0;
    private static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    private String[] prayerNamesList;
    private ArrayList<PrayerModel> prayerModels = new ArrayList<>();
    private PrayerDB db;

    // Declare View components from Layout
    private ListView lv;
    private AppCompatButton locationBtn;
    private AppCompatTextView currentDateText, currentPrayerText;

    private SharedPreferences sp;

    public PrayerTimesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prayer_times, container, false);

        sp = getContext().getSharedPreferences("locationData", Context.MODE_PRIVATE);

        // Getting all the Views from fragment_prayer_times layout
        this.locationBtn = v.findViewById(R.id.prayer_location_btn);
        this.currentDateText = v.findViewById(R.id.current_date_text);
        this.currentPrayerText = v.findViewById(R.id.current_prayer_text);
        this.lv = v.findViewById(R.id.prayer_times_list_view);
        prayerNamesList = getResources().getStringArray(R.array.prayer_names);

        // Set onClickListener to load MapActivity
        this.locationBtn.setOnClickListener(this::onClick);

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        // Build the database
        this.db = Room.databaseBuilder(
                getContext(),
                PrayerDB.class,
                "prayer-database")
                .allowMainThreadQueries()
                .build();
        this.executor = Executors.newFixedThreadPool(4);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPrayerData();
    }

    public void getPrayerData() {
        this.prayerModels = (ArrayList<PrayerModel>) db.prayerDAO().getAllPrayers();

        if (!prayerModels.isEmpty()) {
            updatePrayerTimes(prayerModels);
        } else {
            checkLocationPermissions();
        }
    }

    // Start MapActivity using intents and expect to receive results from this activity
    public void onClick(View view) {
        Intent i = new Intent(view.getContext(), MapsActivity.class);
        startActivityForResult(i, 001);
    }

    public void updatePrayerTimes(ArrayList<PrayerModel> pm) {
        // Set the ListView
        PrayerListAdapter prayerListAdapter = new PrayerListAdapter(lv.getContext(), pm);
        lv.setAdapter(prayerListAdapter);

        // Set the location, current date and prayer texts
        setCurrentPrayerText(pm);
        setCurrentDateText();
        locationBtn.setText(sp.getString("location", ""));
    }

    public void checkLocationPermissions() {
        // If there are no location permissions granted, request location permissions when fragment is loaded
        if (!LocationPermissions.checkIfPermissionsGranted(getActivity(), LOCATION_PERMISSIONS)) {
            requestPermissions(LOCATION_PERMISSIONS, LOCATION_REQUEST_FROM_BUTTON);
        } else {
            fetchLocationData(getId());           // Call fetch location method
        }
    }

    // Method to check permission results for location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If location permissions are denied, take user to MapsActivity and let them know to select a location from the map using Toast
        switch (requestCode) {
            case LOCATION_REQUEST_FROM_BUTTON:
                if (!LocationPermissions.checkIfPermissionResultsGranted(grantResults)) {
                    Intent i = new Intent(getContext(), MapsActivity.class);
                    startActivityForResult(i, 001);
                    Toast.makeText(getContext(), "Please select a location from the map", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // SuppressLint annotation needed as Android Studio is unsure if we checked that the app has been granted location permissions
    @SuppressLint("MissingPermission")
    private void fetchLocationData(int id) {
        // Cancel any ongoing location requests before requesting
        if (locationCallback != null) {
            this.mFusedLocationClient.removeLocationUpdates(locationCallback);
        }

        // Fetch location data on single location update
        if (locationCallback == null) {
            locationCallback = new MyLocationCallback();
            this.mFusedLocationClient.requestLocationUpdates(
                    LocationHelper.singleLocationRequest(LOCATION_PRIORITY),
                    locationCallback,
                    null);
        }
    }

    // Extended location callback to retrieve single location request
    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            // Get last location data and send to updateLocationText method
            updatePrayerTimesFromLocation(locationResult.getLastLocation());
        }
    }

    // Do stuff with this location data
    private void updatePrayerTimesFromLocation(Location l) {
        // Call API with lat and long
        onAPIRequest(l.getLatitude(), l.getLongitude());
    }

    public void onAPIRequest(double latitude, double longitude) {
        // API URL
        String apiUrl = "https://api.pray.zone/v2/times/today.json?longitude=" + longitude + "&latitude=" + latitude + "&elevation=25";
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());

        // Set city and country text in button retrieved from location
        Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
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
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("location", currentLocation);
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
                        Log.e("REQUEST FROM API ERROR", String.valueOf(error));
                    }
                }
        );
        requestQueue.add(apiRequest);
    }

    public void handleResponse(JSONObject items) {
        // Check if ArrayList of PrayerModel is empty, if it is then do this...
        if (prayerModels.size() <= 0) {
            // Initialise some variables
            int listSize = prayerNamesList.length;

            try {
                // Setting JSON Objects and Arrays
                JSONObject resultsJson = items.getJSONObject("results");
                JSONArray datetimeArray = resultsJson.getJSONArray("datetime");

                // Looping through datetime array to get prayer times and storing them in an ArrayList of PrayerModel objects
                for (int i = 0; i < listSize; i++) {
                    JSONObject prayerTimes = datetimeArray.getJSONObject(0).getJSONObject("times");
//                Log.i(prayerNamesList[i],prayerTimes.getString(prayerNamesList[i]));
                    prayerModels.add(new PrayerModel(prayerNamesList[i], prayerTimes.getString(prayerNamesList[i])));
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

        // Populate ListView with the prayer times and names
        updatePrayerTimes(prayerModels);
    }

    public void setCurrentDateText() {
        // Format current date from API, then set the current date text to formatted date
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        LocalDate now = LocalDate.now();
        String output = myFormat.format(now);
        currentDateText.setText(output);
    }

    public void setCurrentPrayerText(ArrayList<PrayerModel> prayers) {
        // Get the current time
        LocalTime timeNow = LocalTime.now();

        // Iterate through the prayer models
        for (PrayerModel pm : prayers) {
            LocalTime prayerTime = LocalTime.parse(pm.getPrayerTime());             // Parse the prayer time into LocalTime
            // If the current prayer time is after the prayer times from the model, then set the current prayer text to that prayer name
            if (timeNow.isAfter(prayerTime)) {
                currentPrayerText.setText(pm.getPrayerName());
//                Log.i("PRAYER TIME",pm.getPrayerName());
            }
        }
    }

    // Receive data from Intents from MapsActivity, if not null, and call onAPIRequest method with Lat/Lng data
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == 001) {

            // Add exception handling
            try {
                Log.i("GOT DATA FROM MAP", data.toString());
                onAPIRequest(data.getDoubleExtra("lat", 0), data.getDoubleExtra("long", 0));
            } catch (Exception e) {
                e.printStackTrace();
                // If cannot retrieve prayer times for the location from the map, then display an error page
                changeInternalFragment(new ErrorFragment(), R.id.main_frag_container);
            }

        } else {
            Log.e("OnActivityResult", "Could not fetch data from MapsActivity");
        }
    }

    // Method to replace internal fragment and set messages to pass through to the fragment
    private void changeInternalFragment(Fragment fragment, int fragmentContainer){
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putString("error title", "Error - unable to find prayer times");
        bundle.putString("error message", "Sorry, could not find prayer times for that location");

        fragment.setArguments(bundle);

        supportFragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();
    }

}