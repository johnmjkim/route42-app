package com.comp6442.groupproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.comp6442.groupproject.BuildConfig;
import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.FirebaseAuthLiveData;
import com.comp6442.groupproject.data.UserViewModel;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.comp6442.groupproject.ui.fragment.FeedFragment;
import com.comp6442.groupproject.ui.fragment.MapFragment;
import com.comp6442.groupproject.ui.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import timber.log.Timber;

/* This activity class is not tied to any specific layout except for the bottom navigation bar.
 *  In other words, this class only contains navigation logic for the bottom nav bar.
 * */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
  private ActionBar toolbar;
  private BottomNavigationView navBarView;
  private MenuItem lastSelected = null;
  private String uid;
  private UserViewModel viewModel;
  // private NavHostFragment
  // private NavGraph

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    uid = getIntent().getStringExtra("uid");

    // bottom navigation
    toolbar = getSupportActionBar();
    navBarView = findViewById(R.id.bottom_navigation_view);
    navBarView.setOnItemSelectedListener(this);
    navBarView.setSelectedItemId(R.id.navigation_profile);

    // Create a ViewModel the first time the system calls an activity's onCreate() method.
    // Re-created activities receive the same MyViewModel instance created by the first activity.
    // If the activity is re-created, it receives the same MyViewModel instance that was created by the first activity.
    // When the owner activity is finished, the framework calls the ViewModel objects's onCleared() method so that it can clean up resources.
    viewModel = new ViewModelProvider(this).get(UserViewModel.class);

    UserRepository.getInstance().getUser(uid).get()
            .addOnSuccessListener(snapshot -> {
              User user = snapshot.toObject(User.class);
              viewModel.setLiveUser(user);
            }).addOnFailureListener(Timber::e);
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
        toolbar.setTitle(R.string.title_fragment_profile);
        break;
      case R.id.navigation_feed:
        fragment = new FeedFragment();
        toolbar.setTitle(R.string.title_fragment_feed);
        break;
      case R.id.navigation_map:
        fragment = new MapFragment();
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
}