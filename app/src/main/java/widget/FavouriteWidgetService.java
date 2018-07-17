package widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;

public class FavouriteWidgetService extends RemoteViewsService {


  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return null;
  }
}

class FavouriteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private static final String LOG_TAG = FavouriteRemoteViewsFactory.class.getSimpleName();

  private int mCount;
  private List<Result> mWidgetItems = new ArrayList<>();
  private Context mContext;
  private int mAppWidgetId;


  public FavouriteRemoteViewsFactory(Context context, Intent intent) {
    this.mContext = context;
    this.mAppWidgetId = intent
        .getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

  }

  @Override
  public void onCreate() {
    // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
    // for example downloading or creating content etc, should be deferred to onDataSetChanged()
    // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

  }

  public void addAll(List<Result> result) {
    if (mWidgetItems != null) {
      mWidgetItems.clear();
    }
    mWidgetItems.addAll(result);
    mCount = mWidgetItems.size();
    Log.i(LOG_TAG, "addAll PlaceId = " + mWidgetItems);
  }

  @Override
  public void onDataSetChanged() {

  }

  @Override
  public void onDestroy() {
    mWidgetItems.clear();

  }

  @Override
  public int getCount() {
    return mWidgetItems.size();
  }

  @Override
  public RemoteViews getViewAt(int position) {
    // We construct a remote views item based on our widget item xml file, and set the
    // text based on the position.

    return null;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 0;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }
}
