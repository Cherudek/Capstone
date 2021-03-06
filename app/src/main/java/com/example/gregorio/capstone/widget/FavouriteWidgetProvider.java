package com.example.gregorio.capstone.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.ui.MainActivity;

import java.util.ArrayList;

import static com.example.gregorio.capstone.ui.FavouritesFragment.WIDGET_INTENT_TAG;

public class FavouriteWidgetProvider extends AppWidgetProvider {

    public static final String INTENT_TO_FAVOURITE_LIST_KEY = "intent to favourite list key";
    public static final String LOG_TAG = FavouriteWidgetProvider.class.getSimpleName();
    private String favourites = "";
    private RemoteViews views;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId, String favouriteList) {

        views = new RemoteViews(context.getPackageName(), R.layout.favourite_widget);
        views.setTextViewText(R.id.appwidget_title, context.getString(R.string.my_turin_favourites));
        views.setTextViewText(R.id.appwidget_favourite_list_tv, favouriteList);

        // click event handler for the title, launches the app when the user clicks on the Widget
        Intent titleIntent = new Intent(context, MainActivity.class);
        titleIntent.putExtra(INTENT_TO_FAVOURITE_LIST_KEY, "Favourite");
        titleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent titlePendingIntent = PendingIntent
                .getActivity(context, 0, titleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_layout, titlePendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        int[] ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            ArrayList<CharSequence> favouriteList = intent
                    .getCharSequenceArrayListExtra(WIDGET_INTENT_TAG);
            if (favouriteList != null) {
                // Iterating through the Ingredients Array List to extract the ingredients to update
                //the Widget ingredients list
                for (int a = 0; a < favouriteList.size(); a++) {
                    CharSequence currentFavourite = favouriteList.get(a);
                    favourites = new StringBuilder().append(favourites).append(currentFavourite.toString())
                            .append("\n").toString();
                }
            }

            //Iterating through all the instances of the widget to update all the Widgets
            for (int i = 0; i < ids.length; i++) {
                updateAppWidget(context, appWidgetManager, ids[i], favourites);

            }
            Log.i(LOG_TAG, "Favourite List Name: " + favourites);
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, FavouriteWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.appwidget_list_view);
        }
    }
}

