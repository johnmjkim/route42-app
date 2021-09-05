package com.comp6442.groupproject;


import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class App42 extends Application {
  private static final String TAG = "Application";
  //  In many apps, there's no need to work with an application class directly. However, there are a few acceptable uses of a custom application class:
  //
  //  Specialized tasks that need to run before the creation of your first activity
  //  Global initialization that needs to be shared across all components (crash reporting, persistence)
  //  Static methods for easy access to static immutable data such as a shared network client object
  //  Note that you should never store mutable shared data inside the Application object since that data might disappear or become invalid at any time. Instead, store any mutable shared data using persistence strategies such as files, SharedPreferences or SQLite.

  // Called when the application is starting, before any other application objects have been created.
  // Overriding this method is totally optional!
  @Override
  public void onCreate() {
    super.onCreate();
    // Required initialization logic here!
    Log.i(TAG, "App started.");
  }

  // Called by the system when the device configuration changes while your component is running.
  // Overriding this method is totally optional!
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  // This is called when the overall system is running low on memory,
  // and would like actively running processes to tighten their belts.
  // Overriding this method is totally optional!
  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }
}