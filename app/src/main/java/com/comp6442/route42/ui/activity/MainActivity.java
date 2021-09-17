package com.comp6442.route42.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.UserViewModel;
import com.comp6442.route42.ui.fragment.FeedFragment;
import com.comp6442.route42.ui.fragment.MapsFragment;
import com.comp6442.route42.ui.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/* This activity class is not tied to any specific layout except for the bottom navigation bar.
 *  In other words, this class only contains navigation logic for the bottom nav bar.
 * */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
  private BottomNavigationView bottomNav;
  private ActionBar toolbar;
  // private NavController navController;
  // private FragmentContainerView fragmentContainerView;

  private MenuItem lastSelected = null;
  private String uid;

  private final List<ListenerRegistration> firebaseListenerRegs = new ArrayList<>();
  UserViewModel userViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);
    uid = getIntent().getStringExtra("uid");

    // Create a ViewModel the first time the system calls an activity's onCreate() method.
    // Re-created activities receive the same MyViewModel instance created by the first activity.
    // If the activity is re-created, it receives the same MyViewModel instance that was created by the first activity.
    // When the owner activity is finished, the framework calls the ViewModel objects's onCleared() method so that it can clean up resources.
    userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    userViewModel.addSnapshotListenerToLiveUser(uid);

    toolbar = getSupportActionBar();
    toolbar.hide();

    // bottom navigation
    bottomNav = findViewById(R.id.bottom_navigation_view);
    bottomNav.setOnItemSelectedListener(this);
    bottomNav.setSelectedItemId(R.id.navigation_profile);

    // navController = Navigation.findNavController(this, R.id.fragment_container_view);
    // NavigationUI.setupWithNavController(bottomNav, navController);
    // fragmentContainerView = findViewById(R.id.fragment_container_view);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      hideSystemUI();
    }
  }

  private void hideSystemUI() {
    // Enables fullscreen
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
  }

  // Shows the system bars by removing all the flags
  // except for the ones that make the content appear under the system bars.
  private void showSystemUI() {
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  /**
   * Called when an item in the bottom navigation menu is selected.
   *
   * @param item The selected item
   * @return true to display the item as the selected item and false if the item should not
   * be selected. Consider setting non-selectable items as disabled preemptively to
   * make them appear non-interactive.
   */
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    if (FirebaseAuthLiveData.getInstance().getAuth().getCurrentUser() == null || this.uid == null) {
      Timber.i("User is not authenticated. Taking user back to log in screen.");
      startActivity(new Intent(this, LogInActivity.class));
    }

    Timber.i("BottomNav Selection: %s", item.toString());

    if (item == lastSelected) return false;
    lastSelected = item;

    Bundle bundle = new Bundle();
    bundle.putString("uid", this.uid);
    Fragment fragment = null;

    switch (item.getItemId()) {
      case R.id.navigation_profile:
        fragment = new ProfileFragment();
        userViewModel.setProfileUser(userViewModel.getLiveUser().getValue());
        toolbar.setTitle(R.string.title_fragment_profile);
        break;
      case R.id.navigation_feed:
        fragment = new FeedFragment();
        toolbar.setTitle(R.string.title_fragment_feed);
        break;
      case R.id.navigation_map:
        fragment = new MapsFragment();
        toolbar.setTitle(R.string.title_fragment_map);
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //detach listeners when Activity destroyed
    firebaseListenerRegs.forEach(ListenerRegistration::remove);
  }
}