package com.comp6442.groupproject.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import com.comp6442.groupproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/* This activity class is not tied to any specific layout except for the bottom navigation bar.
*  In other words, this class only contains navigation logic for the bottom nav bar.
* */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
  private static final String TAG = "MainActivity";
  private ActionBar toolbar;
  private String uid;
  private BottomNavigationView navBarView;
  private NavController navController;
  private AppBarConfiguration appBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    uid = getIntent().getStringExtra("uid");

    // bottom nav
    toolbar = getSupportActionBar();
    if (toolbar != null) toolbar.setTitle(String.format("Hello, %s", uid));

    navBarView = findViewById(R.id.bottom_navigation_view);
    // if moving login activity into this activity as a fragment
    // navBarView.setVisibility(View.GONE);
    navBarView.setSelectedItemId(R.id.navigation_profile);
    navBarView.setOnItemSelectedListener(this::onNavigationItemSelected);
//    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//    assert navHostFragment != null;
//    navController = navHostFragment.getNavController();
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    Log.d(TAG, "BottomNav Selection: " + item.toString());

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