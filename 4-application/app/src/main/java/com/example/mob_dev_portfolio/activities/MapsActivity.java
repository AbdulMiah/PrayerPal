package com.example.mob_dev_portfolio.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.fragments.PrayerTimesFragment;
import com.example.mob_dev_portfolio.location.LocationHelper;
import com.example.mob_dev_portfolio.location.LocationPermissions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Variables for Location
    private static final int LOCATION_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private static final int LOCATION_REQUEST_FROM_MAP = 2;
    private static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    private PrayerTimesFragment prayerFrag;
    private GoogleMap map;
    private SearchView searchView;
    private TextView errorMsg;
    private AppCompatButton backBtn, currentLocationBtn;

    private SharedPreferences userCurrentLocationSp;
    private SharedPreferences locationData;
    private SharedPreferences.Editor userCurrentLocationEditor;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        searchView = findViewById(R.id.sv_location);
        errorMsg = findViewById(R.id.error_text);
        backBtn = findViewById(R.id.back_button);
        currentLocationBtn = findViewById(R.id.current_location_btn);

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        // Setting onClick Listeners
        backBtn.setOnClickListener(this::onClick);

        // Postponing this feature. Show a Toast for now
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCurrentLocationSp = getSharedPreferences("userCurrentLocationData", Context.MODE_PRIVATE);
                if (userCurrentLocationSp.getAll().isEmpty()) {
                    showAlertDialog();
                } else {
                    float lat = userCurrentLocationSp.getFloat("latitude", 0f);
                    float lng = userCurrentLocationSp.getFloat("longitude", 0f);
                    findUsersCurrentLocation(lat, lng);
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        searchQueryAction();

        mapFragment.getMapAsync(this);
    }

    private void addMarkerOnSelectedLocation() {
        locationData = getSharedPreferences("locationData", Context.MODE_PRIVATE);

        if (locationData.getAll().isEmpty()) {
            System.out.println("Location is empty, cannot add marker");
        } else {
            float lat = locationData.getFloat("latitude", 0f);
            float lng = locationData.getFloat("longitude", 0f);

            // Add a marker on the map for the location user currently selected for prayer times
            LatLng latLng = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions().position(latLng).title("Location Selected for Prayer Times"));
        }
    }

    // Finish activity once user clicks back button
    private void onClick(View view) {
        finish();
    }

    // Render map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapClickAction();
        addMarkerOnSelectedLocation();
    }

    public void searchQueryAction() {
        // Set on query listener on search bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                // If search bar is not empty, then get the address data for that location (accepts postcode, city, country, etc)
                if (location != null || !location.equals("")) {
                    Geocoder geo = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geo.getFromLocationName(location, 1);
                        // If the addressList is empty, the location doesn't exist so show error message
                        if (addressList.size() == 0) {
                            errorMsg.setPadding(20, 20, 20, 20);
                            errorMsg.setText("Sorry, could not find that location");
                            return false;
                        }
                        Address address = addressList.get(0);
                        Log.d("LOCATION DATA FROM MAP", address.toString());

                        // Get Lat/Lng from address and save it to an Intent
                        addIntentData(address.getLatitude(), address.getLongitude());

                        // Add a marker on the map for the location so user can verify
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng));

                        // Set duration of zoom animation to 3000ms and finish activity once animation is complete
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10), 3000, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onCancel() {
                                finish();
                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            // Reset error TextView on query text change
            @Override
            public boolean onQueryTextChange(String s) {
                errorMsg.setPadding(0,0,0,0);
                errorMsg.setText("");
                return false;
            }
        });
    }

    public void mapClickAction() {
        // Set an onMapClick listener and pin-point a marker
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();            // Clear all previous markers on the map
                map.addMarker(new MarkerOptions().position(latLng));            // Add the marker

                // Get Lat/Lng from point and save it to an Intent
                addIntentData(latLng.latitude, latLng.longitude);

                // Animate the map camera at level 10 zoom
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10), 3000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onCancel() {
                        finish();
                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }
                });
            }
        });
    }

    public void findUsersCurrentLocation(float lat, float lng) {
        LatLng latLng = new LatLng(lat, lng);
        map.addMarker(new MarkerOptions().position(latLng));            // Add the marker

        // Save Lat/Lng to an Intent
        addIntentData(lat, lng);

        // Animate the map camera at level 10 zoom
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10), 3000, new GoogleMap.CancelableCallback() {
            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onFinish() {
                finish();
            }
        });
    }

    public void addIntentData(double latitude, double longitude) {
        Intent i = new Intent();
        i.putExtra("lat", latitude);
        i.putExtra("long", longitude);
        setResult(001, i);
    }

    // Method to check permission results for location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If location permissions are denied, take user to MapsActivity and let them know to select a location from the map using Toast
        switch (requestCode) {
            case LOCATION_REQUEST_FROM_MAP:
                if (!LocationPermissions.checkIfPermissionResultsGranted(grantResults)) {
                    Toast.makeText(this, "Location permissions are required to use this feature", Toast.LENGTH_SHORT).show();
                } else {
                    fetchLocationData();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    private void fetchLocationData() {
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
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            float lat = (float) locationResult.getLastLocation().getLatitude();
            float lng = (float) locationResult.getLastLocation().getLongitude();

            // Storing lat/lng from current location from MapActivity into SharedPreference
            userCurrentLocationSp = getSharedPreferences("userCurrentLocationData", Context.MODE_PRIVATE);
            userCurrentLocationEditor = userCurrentLocationSp.edit();
            userCurrentLocationEditor.putFloat("latitude", lat);
            userCurrentLocationEditor.putFloat("longitude", lng);
            userCurrentLocationEditor.commit();

            // Zoom into map of current location
            findUsersCurrentLocation(lat, lng);
        }
    }


    // Create an AlertDialog to let user know that this app requires Location Permissions for 'Use Current Location' feature
    public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("PrayerPal needs you to allow Location permissions in order to use this feature." +
                "\n\nTo enable location permissions, go to Settings > Apps > PrayerPal > Permissions > Location > Allow (or 'ask every time')");
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Try to request permissions again after user clicks 'Okay' on AlertDialog
                        requestPermissions(LOCATION_PERMISSIONS, LOCATION_REQUEST_FROM_MAP);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // Send Lat/Lng results from this activity to the PrayerTimesFragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        prayerFrag.onActivityResult(requestCode, resultCode, data);
    }
}