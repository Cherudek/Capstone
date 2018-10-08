package com.example.gregorio.capstone.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.model.placeId.Result;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ListRemoteViewFactory implements RemoteViewsFactory {

  private static final String LOG_TAG = ListRemoteViewFactory.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITES_NODE = "Favourites";
  private String mApiKey;
  private Context context;
  private DatabaseReference favouriteDbRef;
  private String[] strings = {"1", "2", "3", "4", "5"};
  private List<Result> favouritesPlaceId = new ArrayList<>();


  public ListRemoteViewFactory(Context context, List<Result> favouriteList) {
    this.context = context;
    this.favouritesPlaceId = favouriteList;
  }

  public ListRemoteViewFactory(Context context, Intent intent) {
    this.context = context;
    int appWidgetId = intent
        .getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

  }

  @Override
  public void onCreate() {


  }


  @Override
  public void onDataSetChanged() {




  }

  @Override
  public void onDestroy() {

  }

  @Override
  public int getCount() {
    if (favouritesPlaceId != null) {
      Log.i(LOG_TAG, "getCount: " + favouritesPlaceId.size());
      return favouritesPlaceId.size();
    } else {
      return 0;
    }

  }

  @Override
  public RemoteViews getViewAt(int position) {

    if (favouritesPlaceId.size() == 0) {
      return null;
    }
    Result result = favouritesPlaceId.get(position);
    String photoReference = result.getPhotos().get(0).getPhotoReference();
    String address = result.getVicinity();
    String name = result.getName();
    String placeId = result.getPlaceId();
    String photoUrl =
        PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + mApiKey;

    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_favourite_item);
    views.setTextViewText(R.id.widget_favourite_place_name, name);
    views.setTextViewText(R.id.widget_favourite_place_address, address);

//    Intent fillInIntent = new Intent();
//    fillInIntent.putExtra(FavouriteWidgetProvider.EXTRA_LABEL, name);
//    fillInIntent.putExtra(FavouriteWidgetProvider.EXTRA_LABEL2, address);
//    views.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);


    return views;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }
}
