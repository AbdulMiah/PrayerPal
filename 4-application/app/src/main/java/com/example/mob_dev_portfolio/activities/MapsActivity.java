package com.example.mob_dev_portfolio.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.mob_dev_portfolio.location.LocationPermissions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_FROM_MAP = 2;
    private static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private PrayerTimesFragment prayerFrag;
    private GoogleMap map;
    private SearchView searchView;
    private TextView errorMsg;
    private AppCompatButton backBtn;
    private AppCompatButton currentLocationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        searchView = findViewById(R.id.sv_location);
        errorMsg = findViewById(R.id.error_text);
        backBtn = findViewById(R.id.back_button);
        currentLocationBtn = findViewById(R.id.current_location_btn);

        // Setting onClick Listeners
        backBtn.setOnClickListener(this::onClick);

        // Postponing this feature. Show a Toast for now
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showAlertDialog();
                Toast.makeText(view.getContext(), "This feature is still under construction", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        searchQueryAction();

        mapFragment.getMapAsync(this);
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
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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