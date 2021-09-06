package com.comp6442.groupproject.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
  private MenuItem lastSelected = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    uid = getIntent().getStringExtra("uid");

    // bottom nav
    toolbar = getSupportActionBar();
    if (toolbar != null) toolbar.setTitle(String.format("Hello, %s", uid));

    navBarView = findViewById(R.id.bottom_navigation_view);
    navBarView.setOnItemSelectedListener(this);
    navBarView.setSelectedItemId(R.id.navigation_profile);
  }

  /**
   * Called when an item in the bottom navigation menu is selected.
   *
   * @param item The selected item
   *
   * @return true to display the item as the selected item and false if the item should not
   *         be selected. Consider setting non-selectable items as disabled preemptively to
   *         make them appear non-interactive.
   */
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

    if (item == lastSelected) return false;
    else lastSelected = item;

    Bundle bundle = new Bundle();
    bundle.putString("uid", uid);

    Fragment fragment = null;

    switch (item.getItemId()) {

      case R.id.navigation_profile:
        fragment = new ProfileFragment();
        toolbar.setTitle(R.string.title_fragment_profile);
        break;

      case R.id.navigation_feed:
        fragment = new FeedFragment();
        toolbar.setTitle(R.string.title_fragment_feed);
        break;
    }

    assert fragment != null;

    fragment.setArguments(bundle);
    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit();

    return true;
  }
}