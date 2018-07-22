package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.gregorio.capstone.MainActivity;
import com.example.gregorio.capstone.R;
import java.util.ArrayList;


public class FavouriteWidgetProvider extends AppWidgetProvider {

  public static final String EXTRA_LABEL = "NAME_TEXT";
  public static final String EXTRA_LABEL2 = "ADDRESS_TEXT";
  public static final String INTENT_KEY = "intent key";

  public static final String LOG_TAG = FavouriteWidgetProvider.class.getSimpleName();


  public static void sendRefreshBroadcast(Context context) {
    Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    intent.setComponent(new ComponentName(context, FavouriteWidgetProvider.class));
    context.sendBroadcast(intent);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      RemoteViews views = new RemoteViews(

          context.getPackageName(),
          R.layout.favourite_widget

      );

      // Async Intent Service Thread to fetch Data from Firebase Db
      context.startService(new Intent(context, FirebaseToWidgetService.class));

      // click event handler for the title, launches the app when the user clicks on title
      Intent titleIntent = new Intent(context, MainActivity.class);
      PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, 0);
      views.setOnClickPendingIntent(R.id.appwidget_layout, titlePendingIntent);

      Intent intent = new Intent(context, ListWidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      views.setRemoteAdapter(R.id.appwidget_list_view, intent);

      // template to handle the click listener for each item
//        Intent clickIntentTemplate = new Intent(context, MainActivity.class);
//        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
//            .addNextIntentWithParentStack(clickIntentTemplate)
//            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setPendingIntentTemplate(R.id.appwidget_list_view, clickPendingIntentTemplate);

      appWidgetManager.updateAppWidget(appWidgetId, views);
    }

  }


  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    // When the user deletes the widget, delete the preference associated with it.
    for (int appWidgetId : appWidgetIds) {
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    final String action = intent.getAction();
    if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
      // refresh all your widgets
      ArrayList<CharSequence> favourites = intent.getCharSequenceArrayListExtra(INTENT_KEY);
      Log.i(LOG_TAG, "Favourite List Name: " + favourites);
      AppWidgetManager mgr = AppWidgetManager.getInstance(context);
      ComponentName cn = new ComponentName(context, FavouriteWidgetProvider.class);
      mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.appwidget_list_view);
    }
  }
}

