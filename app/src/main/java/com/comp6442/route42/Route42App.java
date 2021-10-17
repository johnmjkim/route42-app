package com.comp6442.route42;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.activity.LogInActivity;
import com.comp6442.route42.ui.activity.MainActivity;
import com.comp6442.route42.utils.Crypto;
import com.comp6442.route42.utils.CustomLogger;
import com.comp6442.route42.utils.tasks.TaskCreatePosts;
import com.comp6442.route42.utils.tasks.TaskCreatePostsDemo;
import com.comp6442.route42.utils.tasks.TaskCreateUsers;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;


public class Route42App extends Application {
  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
  private FirebaseAuth mAuth;

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onCreate() {
    super.onCreate();
    mAuth = FirebaseAuthLiveData.getInstance().getAuth();
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

    // initialize Timber logger
    Timber.plant(new CustomLogger());

    if (BuildConfig.EMULATOR) {
      Timber.i("Application starting on DEBUG mode");
    } else {
      Timber.i("Application starting");
    }

    try {
      createTestUser();
    } catch (Exception e) {
      Timber.e("Could not create test user: %s", e.getMessage());
    }

    if (BuildConfig.skipLogin) {
      mAuth.signInWithEmailAndPassword(BuildConfig.testUserEmail, Crypto.encryptAndEncode(BuildConfig.testUserPassword))
              .addOnSuccessListener(authResult -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("uid", mAuth.getUid());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
              });
    } else {
      if (mAuth.getCurrentUser() != null) mAuth.signOut();
      Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    mAuth.signOut();
    if (!executor.isTerminated()) executor.shutdownNow();
  }

  /**
   * Create test user, and insert sample data if loadData flag is set to true
   */
  public void createTestUser() {
    Timber.i("Creating test user.");
    User testUser = new User(
            null,
            BuildConfig.testUserEmail,
            "test_user",
            BuildConfig.testUserPassword
    );
    testUser.setProfilePicUrl("https://images.unsplash.com/photo-1512327605305-64e5ce63b346?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");

    Timber.i("Creating test user in Firebase Auth.");
    mAuth.createUserWithEmailAndPassword(testUser.getEmail(), Crypto.encryptAndEncode(testUser.getPassword()))
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
                Timber.w("Failed to create test user in Firebase Auth");
              }
              insertData();
            });
  }

  private void insertData() {
    if (BuildConfig.loadData) {
      Timber.i("Loading sample user and post data");
      executor.execute(new TaskCreateUsers(this, R.raw.users));
      executor.execute(new TaskCreatePosts(this, R.raw.posts));
    }

    if (BuildConfig.DEMO) {
      Timber.i("Simulating realtime posts: create %d posts every %d seconds until %d posts are created",
              BuildConfig.batchSize,
              BuildConfig.intervalLengthInSeconds,
              BuildConfig.demoPostLimit
      );
      executor.scheduleAtFixedRate(
              new TaskCreatePostsDemo(this, R.raw.posts_demo),
              0,
              BuildConfig.intervalLengthInSeconds,
              TimeUnit.SECONDS
      );
    }
  }
}