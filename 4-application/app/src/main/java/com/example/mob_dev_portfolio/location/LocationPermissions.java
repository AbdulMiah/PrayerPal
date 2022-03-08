package com.example.mob_dev_portfolio.location;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class LocationPermissions {

    // Check whether application has been granted a set of permissions by the user
    public static boolean checkIfPermissionsGranted(Activity activity, String[] permissions) {
        int maxPermissions = permissions.length;

        // Loop through set of permissions and return false is permissions are denied
        for (int i=0; i<maxPermissions; i++) {
            if (ActivityCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;            // Otherwise, return true
    }

    // Check whether user accepted all permissions
    public static boolean checkIfPermissionResultsGranted(int[] results) {
        if (results.length == 0) {
            return false;
        } else {
            // Loop through results and return false if permissions are denied
            for (int r : results) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;            // Otherwise, return true
    }
}
