package widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class ListWidgetService extends RemoteViewsService {

  private static final String LOG_TAG = ListWidgetService.class.getSimpleName();
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    Log.i(LOG_TAG, "onGetViewFactory: " + "Service called");
    return new ListRemoteViewFactory(this.getApplicationContext(), intent);
  }
}
