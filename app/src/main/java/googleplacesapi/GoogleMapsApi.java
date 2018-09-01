package googleplacesapi;

import static com.google.android.gms.location.places.Places.getPlaceDetectionClient;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import com.example.gregorio.capstone.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;

public class GoogleMapsApi extends AppCompatActivity {

  private static final int REQUEST_PLACE_PICKER = 1;
  private GoogleApiClient mGoogleApiClient;
  private PlaceDetectionClient mPlaceDetectionClient;
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private String locationProvider;


  public GoogleMapsApi() {
  }

  public boolean CheckGooglePlayServices(Context context, Activity activity) {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(context);
    if (result != ConnectionResult.SUCCESS) {
      if (googleAPI.isUserResolvableError(result)) {
        googleAPI.getErrorDialog(activity, result,
            0).show();
        Snackbar snackbar = Snackbar
            .make(activity.getCurrentFocus(), R.string.google_play_services_failure,
                Snackbar.LENGTH_LONG);
        snackbar.show();
      }
      return false;
    }
    return true;
  }

  public void GoogleApiClient(Context context) {
    // Construct a PlaceDetectionClient.
    mPlaceDetectionClient = getPlaceDetectionClient(context);
    // Construct a FusedLocationProviderClient.
    mFusedLocationProviderClient = LocationServices
        .getFusedLocationProviderClient(context);
    // Set up the API client for Places API
    mGoogleApiClient = new GoogleApiClient.Builder(context)
        .addApi(Places.GEO_DATA_API)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();
  }

  public void DisconnectGoogleApiClient(Context context) {
    // Construct a PlaceDetectionClient.
    mPlaceDetectionClient = getPlaceDetectionClient(context);
    // Construct a FusedLocationProviderClient.
    mFusedLocationProviderClient = LocationServices
        .getFusedLocationProviderClient(context);
    mGoogleApiClient.disconnect();
  }

}
