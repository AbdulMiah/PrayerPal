package com.example.mob_dev_portfolio.location;

import com.google.android.gms.location.LocationRequest;

public class LocationHelper {

    // Create a method for location request for a single update
    public static LocationRequest singleLocationRequest(int priority) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(priority);
        return locationRequest;
    }
}
