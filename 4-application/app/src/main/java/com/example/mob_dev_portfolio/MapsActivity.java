package com.example.mob_dev_portfolio;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private PrayerTimesFragment prayerFrag;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private SearchView searchView;
    private TextView errorMsg;
    private AppCompatButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        searchView = findViewById(R.id.sv_location);
        errorMsg = findViewById(R.id.error_text);
        backBtn = findViewById(R.id.back_button);

        // Setting onClick Listener on back button
        backBtn.setOnClickListener(this::onClick);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

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
//                        Log.d("LOCATION DATA FROM MAP", address.toString());

                        // Get Lat/Lng from address and save it to an Intent
                        Intent i = new Intent();
                        i.putExtra("lat", address.getLatitude());
                        i.putExtra("long", address.getLongitude());
                        setResult(001, i);

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
    }

    // Send Lat/Lng results from this activity to the PrayerTimesFragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        prayerFrag.onActivityResult(requestCode, resultCode, data);
    }
}