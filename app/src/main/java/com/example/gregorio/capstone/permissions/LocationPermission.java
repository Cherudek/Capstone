package com.example.gregorio.capstone.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class LocationPermission extends AppCompatActivity {

    public LocationPermission() {
    }

    public final boolean checkLocalPermission(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            int MY_LOCATION_REQUEST_CODE = 101;
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }
            return false;
        } else {
            return true;
        }
    }
}
