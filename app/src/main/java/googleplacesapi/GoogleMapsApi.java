package googleplacesapi;

import static com.google.android.gms.location.places.Places.getPlaceDetectionClient;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;

public class GoogleMapsApi {

  public GoogleMapsApi() {
  }

  public boolean CheckGooglePlayServices(Context context, Activity activity) {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(context);
    if (result != ConnectionResult.SUCCESS) {
      if (googleAPI.isUserResolvableError(result)) {
        googleAPI.getErrorDialog(activity, result,
            0).show();
      }
      return false;
    }
    return true;
  }

  public void GoogleApiClient(Context context) {
    // Construct a PlaceDetectionClient.
    PlaceDetectionClient mPlaceDetectionClient = getPlaceDetectionClient(context);
    // Construct a FusedLocationProviderClient.
    FusedLocationProviderClient mFusedLocationProviderClient = LocationServices
        .getFusedLocationProviderClient(context);
    // Set up the API client for Places API
    GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
        .addApi(Places.GEO_DATA_API)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();
  }
}
