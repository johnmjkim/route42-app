package com.comp6442.route42;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.activity.LogInActivity;
import com.comp6442.route42.ui.activity.MainActivity;
import com.comp6442.route42.utils.CustomLogger;
import com.comp6442.route42.utils.TaskCreatePosts;
import com.comp6442.route42.utils.TaskCreateUsers;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;


/**
 * In many apps, there's no need to work with an application class directly.
 * However, there are a few acceptable uses of a custom application class:
 * Specialized tasks that need to run before the creation of your first activity
 * Global initialization that needs to be shared across all components (crash reporting, persistence)
 * Static methods for easy access to static immutable data such as a shared network client object
 * Note that you should never store mutable shared data inside the Application object since
 * that data might disappear or become invalid at any time.
 * Instead, store any mutable shared data using persistence strategies such as files,
 * SharedPreferences or SQLite.
 */
public class Route42App extends Application {
  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
  private FirebaseAuth mAuth;

  // Called when the application is starting, before any other application objects have been created.
  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onCreate() {
    super.onCreate();
    mAuth = FirebaseAuthLiveData.getInstance().getAuth();
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

    // initialize Timber logger in application class
    Timber.plant(new CustomLogger());

    if (BuildConfig.EMULATOR) {
      Timber.i("Application starting on DEBUG mode");
    } else {
      Timber.i("Application starting");
    }

    // create test user, sign out and take user to log in screen
    if (BuildConfig.loadData) createTestUser();

    Intent intent;
    if (BuildConfig.EMULATOR && BuildConfig.skipLogin) {
      // sign in as test user
      mAuth.signInWithEmailAndPassword(BuildConfig.testUserEmail, BuildConfig.testUserPassword);
      intent = new Intent(getApplicationContext(), MainActivity.class);
      intent.putExtra("uid", mAuth.getUid());
    } else {
      // sign out and take user to log in screen
      if (mAuth.getCurrentUser() != null) mAuth.signOut();
      intent = new Intent(getApplicationContext(), LogInActivity.class);
    }

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
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

  @Override
  public void onTerminate() {
    super.onTerminate();
    mAuth.signOut();
    if (!executor.isTerminated()) executor.shutdownNow();
  }

  public void createTestUser() {
    // create test user, and launch executor task if needed for demo / loadData
    Timber.i("Creating test user.");
    User testUser = new User(
            null,
            BuildConfig.testUserEmail,
            "test_user",
            BuildConfig.testUserPassword
    );
    testUser.setProfilePicUrl("https://images.unsplash.com/photo-1512327605305-64e5ce63b346?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");

    Timber.i("Creating test user in Firebase Auth.");
    mAuth.createUserWithEmailAndPassword(testUser.getEmail(), testUser.getPassword())
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                AuthResult authResult = task.getResult();
                assert authResult != null;
                FirebaseUser firebaseUser = authResult.getUser();
                assert firebaseUser != null;

                testUser.setId(firebaseUser.getUid());
                UserRepository.getInstance().setOne(testUser);

                Timber.i("Created test user in Firebase Auth.");
              } else {
                Timber.w("Could not create test user in Firebase Auth");
                Timber.e(task.getException());
              }
              if (BuildConfig.loadData) insertData();
            });
  }

  private void insertData() {
    if (BuildConfig.loadData) {
      Timber.i("Loading sample data");

      TaskCreateUsers insertUsers = new TaskCreateUsers(this, BuildConfig.EMULATOR);
      TaskCreatePosts livePostTask = new TaskCreatePosts(this, BuildConfig.EMULATOR, BuildConfig.DEMO);

      executor.execute(insertUsers);

      if (!BuildConfig.DEMO) executor.schedule(livePostTask, 5, TimeUnit.SECONDS);
      else {
        Timber.i("Simulating realtime posts: create %d posts every %d seconds until %d posts are created",
                BuildConfig.batchSize,
                BuildConfig.intervalLengthInSeconds,
                BuildConfig.demoPostLimit
        );
        executor.scheduleAtFixedRate(
                livePostTask,
                2,
                BuildConfig.intervalLengthInSeconds,
                TimeUnit.SECONDS
        );
      }
    }
  }
}