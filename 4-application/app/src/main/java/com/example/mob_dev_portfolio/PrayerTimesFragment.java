package com.example.mob_dev_portfolio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

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
import com.example.mob_dev_portfolio.adapters.PrayerListAdapter;
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

public class PrayerTimesFragment extends Fragment {

    private static final int LOCATION_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private static final int LOCATION_REQUEST_FROM_BUTTON = 0;
    private static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationCallback locationCallback;

    private String[] prayerNamesList;
    private ArrayList<PrayerModel> prayerModels = new ArrayList<PrayerModel>();

    private FusedLocationProviderClient mFusedLocationClient;

    private ListView lv;
    private PrayerListAdapter prayerListAdapter;
    private AppCompatButton locationBtn;
    private AppCompatTextView currentDateText;
    private AppCompatTextView currentPrayerText;

    public PrayerTimesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prayer_times, container, false);

        // Getting all the Views from fragment_prayer_times layout
        this.locationBtn = v.findViewById(R.id.prayer_location_btn);
        this.currentDateText = v.findViewById(R.id.current_date_text);
        this.currentPrayerText = v.findViewById(R.id.current_prayer_text);
        this.lv = v.findViewById(R.id.prayer_times_list_view);
        prayerNamesList = getResources().getStringArray(R.array.prayer_names);

        // Temporary set onClickListener to refresh API request on button click
        this.locationBtn.setOnClickListener(this::onClick);

        // Run the API request when fragment is loaded
//        onAPIRequest(v, 51.4815, -3.1790);

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        // If there are no location permissions granted, request location permissions when fragment is loaded
        if (!LocationPermissions.checkIfPermissionsGranted(this.getActivity(), LOCATION_PERMISSIONS)) {
            requestPermissions(LOCATION_PERMISSIONS, LOCATION_REQUEST_FROM_BUTTON);
        } else {
            fetchLocationData(v.getId());           // Call fetch location method
        }

        return v;
    }

    public void onClick(View view) {
        Intent i = new Intent(view.getContext(), MapsActivity.class);
        startActivityForResult(i, 001);

//        if (!LocationPermissions.checkIfPermissionsGranted(this.getActivity(), LOCATION_PERMISSIONS)) {
//            requestPermissions(LOCATION_PERMISSIONS, LOCATION_REQUEST_FROM_BUTTON);
//        } else {
//            fetchLocationData(view.getId());
//        }
    }

    // Method to check permission results for location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Make a Toast to say location permissions are needed if location permissions are denied
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
    private void updatePrayerTimesFromLocation(Location l){
        // Call API with lat and long
        onAPIRequest(getView(), l.getLatitude(), l.getLongitude());

        // If not already set, use Geocoder to get city and country name from location
        if (locationBtn.getText().equals("")) {
            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geo.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                String city = addresses.get(0).getSubAdminArea();
                String country = addresses.get(0).getCountryName();

                // Setting current location text in button to prayer location from API
                String currentLocation = city.concat(", "+country);
                this.locationBtn.setText(currentLocation);
                Log.d("CURRENT LOCATION", currentLocation);
            }
        }
    }

    public void onAPIRequest(View view, double latitude, double longitude) {
        // API URL
        String apiUrl = "https://api.pray.zone/v2/times/today.json?longitude="+longitude+"&latitude="+latitude+"&elevation=25";
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
            String currentLocation = city.concat(", "+country);
            this.locationBtn.setText(currentLocation);
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
            String currentDate = "";
            int listSize = prayerNamesList.length;

            try {
                // Setting JSON Objects and Arrays
                JSONObject resultsJson = items.getJSONObject("results");
                JSONArray datetimeArray = resultsJson.getJSONArray("datetime");

                // Looping through datetime array to get prayer times and storing them in an ArrayList of PrayerModel objects
                for (int i=0; i<listSize; i++) {
                    JSONObject prayerTimes = datetimeArray.getJSONObject(0).getJSONObject("times");
//                Log.i(prayerNamesList[i],prayerTimes.getString(prayerNamesList[i]));
                    prayerModels.add(new PrayerModel(prayerNamesList[i], prayerTimes.getString(prayerNamesList[i])));
                }

                // Get current date from API
                currentDate = datetimeArray.getJSONObject(0).getJSONObject("date").getString("gregorian");

                // Set the location and current date texts
                setCurrentDateFromAPI(currentDate);

            // Catch any JSONExceptions and Log to console
            } catch (JSONException e) {
                Log.e("ERROR!", e.toString());
            }
        // If ArrayList of PrayerModel is not empty, clear list and recall handleResponse() method
        } else {
            prayerModels.clear();
            handleResponse(items);
        }

        // Set current prayer text
        setCurrentPrayerText();

        // Populate ListView with the prayer times and names
        prayerListAdapter = new PrayerListAdapter(lv.getContext(), prayerModels);
        this.lv.setAdapter(prayerListAdapter);
    }

    public void setCurrentDateFromAPI(String currentDate) {
        // Format current date from API, then set the current date text to formatted date
        DateTimeFormatter apiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        LocalDate formatDate = LocalDate.parse(currentDate, apiFormat);
        String output = myFormat.format(formatDate);
        currentDateText.setText(output);
//        Log.i("CURRENT DATE IS...", output);
    }

    public void setCurrentPrayerText() {
        // Get the current time
        LocalTime timeNow = LocalTime.now();

        // Iterate through the prayer models
        for (PrayerModel pm : prayerModels) {
            LocalTime prayerTime = LocalTime.parse(pm.getPrayerTime());             // Parse the prayer time into LocalTime
            // If the current prayer time is after the prayer times from the model, then set the current prayer text to that prayer name
            if (timeNow.isAfter(prayerTime)) {
                currentPrayerText.setText(pm.getPrayerName());
//                Log.i("PRAYER TIME",pm.getPrayerName());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onAPIRequest(getView(), data.getDoubleExtra("lat",0), data.getDoubleExtra("long",0));
    }

}