package com.comp6442.groupproject.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.ui.fragments.FeedFragment;
import com.comp6442.groupproject.ui.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/* This activity class is not tied to any specific layout except for the bottom navigation bar.
 *  In other words, this class only contains navigation logic for the bottom nav bar.
 * */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
  private static final String TAG = "MainActivity";
  private ActionBar toolbar;
  private String uid;
  private BottomNavigationView navBarView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    uid = getIntent().getStringExtra("uid");

    // bottom nav
    toolbar = getSupportActionBar();
    if (toolbar != null) toolbar.setTitle(String.format("Hello, %s", uid));

    navBarView = findViewById(R.id.bottom_navigation_view);
    navBarView.setOnItemSelectedListener(this::onNavigationItemSelected);
    navBarView.setSelectedItemId(R.id.navigation_profile);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    Log.d(TAG, "BottomNav Selection: " + item.toString());

    if (uid == null) {
      try {
        Log.w(TAG, "uid is null");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    ProfileFragment profile = new ProfileFragment();
    FeedFragment feed = new FeedFragment();
    Bundle bundle = new Bundle();
    bundle.putString("uid", uid);

    switch (item.getItemId()) {
      case R.id.navigation_profile:
        profile.setArguments(bundle);
        toolbar.setTitle("Profile");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, profile)
                .commit();
        return true;

      case R.id.navigation_feed:
        feed.setArguments(bundle);
        toolbar.setTitle("Feed");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, feed)
                .commit();
        return true;

      default:
        Log.w(TAG, "Unrecognized selection: " + item.toString());
        return false;
    }
  }
}