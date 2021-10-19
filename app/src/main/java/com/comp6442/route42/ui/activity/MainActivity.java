package com.comp6442.route42.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;

import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.ui.fragment.ActiveMapFragment;
import com.comp6442.route42.ui.fragment.FeedFragment;
import com.comp6442.route42.ui.fragment.PointMapFragment;
import com.comp6442.route42.ui.fragment.ProfileFragment;
import com.comp6442.route42.ui.viewmodel.ActiveMapViewModel;
import com.comp6442.route42.ui.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/* This activity class is not tied to any specific layout except for the bottom navigation bar.
 *  In other words, this class only contains navigation logic for the bottom nav bar.
 * */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
  private final List<ListenerRegistration> firebaseListenerRegs = new ArrayList<>();
  UserViewModel userViewModel;
  //  private NavController navController;
  //  private FragmentContainerView fragmentContainerView;
  private BottomNavigationView bottomNav;
  private ActionBar toolbar;
  private MenuItem lastSelected = null;
  private String uid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    uid = getIntent().getStringExtra("uid");

    // Create a ViewModel the first time the system calls an activity's onCreate() method.
    // Re-created activities receive the same MyViewModel instance created by the first activity.
    // If the activity is re-created, it receives the same MyViewModel instance that was created by the first activity.
    // When the owner activity is finished, the framework calls the ViewModel objects's onCleared() method so that it can clean up resources.
    userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    userViewModel.addSnapshotListenerToLiveUser(uid);

    // toolbar = getSupportActionBar();
    // toolbar.hide();
    setCreateActivityBtn();

    // bottom navigation
    bottomNav = findViewById(R.id.bottom_navigation_view);
    bottomNav.setOnItemSelectedListener(this);
    bottomNav.setSelectedItemId(R.id.navigation_profile);

    // NavigationUI.setupWithNavController(bottomNav, navController);
    // fragmentContainerView = findViewById(R.id.fragment_container_view);
  }

  private void setCreateActivityBtn() {
    this.findViewById(R.id.Btn_Create_Activity).setOnClickListener( event -> {
      createActivityBtnClickHandler();
    });
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
//    if (hasFocus) hideSystemUI();
//    else showSystemUI();
  }

  private void hideSystemUI() {
    // Enables fullscreen
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(
            // immersive mode full screen
            View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    // | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    // force back button to appear
                    | View.SYSTEM_UI_FLAG_VISIBLE
//                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
    );
  }

  // Shows the system bars by removing all the flags
  // except for the ones that make the content appear under the system bars.
  private void showSystemUI() {
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }
  public void createActivityBtnClickHandler() {
//    Timber.i("Create activity btn clicked.");
    Activity activityData = new ViewModelProvider(this).get(ActiveMapViewModel.class).getActivityData();
    MainActivity self = this;
    if (activityData == null) {
      AlertDialog alertDialog = new MaterialAlertDialogBuilder(new ContextThemeWrapper(this, R.style.AlertDialog_AppCompat)).setTitle("Choose Activity Type")
              .setItems(Activity.Activity_Type.getValues(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  Bundle bundle = new Bundle();
                  bundle.putInt("activity", i);
                  bundle.putString("uid", self.uid );
                  Fragment fragment = new ActiveMapFragment();
                  fragment.setArguments(bundle);
//    toolbar.setTitle("Activity");
                  getSupportFragmentManager()
                          .beginTransaction()
                          .replace(R.id.fragment_container_view, fragment)
                          .commit();
                }
              }).create();
      alertDialog.show();

    } else{
      ActiveMapViewModel activeMapViewModel = new ViewModelProvider(this).get(ActiveMapViewModel.class);
      Bundle bundle = new Bundle();
      bundle.putInt("activity", activeMapViewModel.getActivityType().getValue());
      bundle.putString("uid", self.uid );
      Fragment fragment = new ActiveMapFragment();
      fragment.setArguments(bundle);
//    toolbar.setTitle("Activity");
      getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container_view, fragment)
              .commit();
    }

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
    Fragment fragment;

    if (item == lastSelected) {
      fragment =  selectMenuItemFragment(lastSelected);
    } else{
      fragment = selectMenuItemFragment(item);

      lastSelected = item;
    }

    Bundle bundle = new Bundle();
    bundle.putString("uid", this.uid);
    fragment.setArguments(bundle);

    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit();

    return true;
  }
  public Fragment selectMenuItemFragment(MenuItem item) {
    Fragment fragment = null;

    switch (item.getItemId()) {
      case R.id.navigation_profile:
        fragment = new ProfileFragment();
        userViewModel.setProfileUser(userViewModel.getLiveUser().getValue());
//        toolbar.setTitle(R.string.title_fragment_profile);
        break;
      case R.id.navigation_feed:
        fragment = new FeedFragment();
//        toolbar.setTitle(R.string.title_fragment_feed);
        break;
      case R.id.navigation_map:
        fragment = new PointMapFragment();
//        toolbar.setTitle(R.string.title_fragment_map);
        break;
    }
    return fragment;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //detach listeners when Activity destroyed
    firebaseListenerRegs.forEach(ListenerRegistration::remove);
  }
}