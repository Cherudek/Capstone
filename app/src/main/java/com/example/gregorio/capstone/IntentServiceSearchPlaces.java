package com.example.gregorio.capstone;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class IntentServiceSearchPlaces extends IntentService {

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */

  private static final String LOG_TAG = IntentServiceSearchPlaces.class.getSimpleName();
  public IntentServiceSearchPlaces(String name) {
    super(name);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    Log.i(LOG_TAG, "onHandleIntent Thread = " + Thread.currentThread().getName());

  }
}
