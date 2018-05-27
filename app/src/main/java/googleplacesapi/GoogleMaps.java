package googleplacesapi;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GoogleMaps extends AppCompatActivity {

  public GoogleMaps() {
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


}
