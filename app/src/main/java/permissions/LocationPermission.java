package permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class LocationPermission extends AppCompatActivity {

  private final int MY_LOCATION_REQUEST_CODE = 101;

  public LocationPermission() {
  }

  // Check if local permission is enabled and ask the user to enable it if not to access app functionality
  public final boolean checkLocalPermission(Context context, Activity activity) {
    if (ContextCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      // Asking user if explanation is needed
      if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
          Manifest.permission.ACCESS_FINE_LOCATION)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

        //Prompt the user once explanation has been shown
        ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_LOCATION_REQUEST_CODE);
      } else {
        // No explanation needed, we can request the permission.
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
