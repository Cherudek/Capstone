package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.example.gregorio.capstone.MainActivity;
import com.example.gregorio.capstone.R;


public class FavouriteWidgetProvider extends AppWidgetProvider {

  public static final String EXTRA_LABEL = "NAME_TEXT";
  public static final String EXTRA_LABEL2 = "ADDRESS_TEXT";
  public static final String INTENT_KEY = "intent key";

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
      int appWidgetId) {

    // Construct the RemoteViews object
    RemoteViews views;

    Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
    int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
    // if(width<300){
    // rv = getSingleRemoteView(context);
    //  } else {
    views = getListRemoteView(context);
    //  }
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  private static RemoteViews getSingleRemoteView(Context context) {
    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favourite_widget);
    // Set a Pending intent to launch the Favourite Fragment.
    Intent intent = new Intent(context, MainActivity.class);
    intent.putExtra(INTENT_KEY, "Favourite");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);
    return views;
  }

  private static RemoteViews getListRemoteView(Context context) {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favourite_widget);
    // Set the ListWidgetService intent as the adapter for the ListView
    Intent intent = new Intent(context, ListWidgetService.class);
    views.setRemoteAdapter(R.id.appwidget_list_view, intent);
    // set the FavouriteDetails fragment to launch when clicked

    Intent appIntent = new Intent(context, MainActivity.class);
    appIntent.putExtra(INTENT_KEY, "Favourite");
    appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent appPendingIntent = PendingIntent
        .getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    views.setOnClickPendingIntent(R.id.appwidget_layout, appPendingIntent);
    // Handle Empty List View
    views.setEmptyView(R.id.appwidget_list_view, R.id.appwidget_background);
    return views;

  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);

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
  }
}

