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
import com.comp6442.route42.utils.AESCrypt;
import com.comp6442.route42.utils.CustomLogger;
import com.comp6442.route42.utils.tasks.TaskCreatePosts;
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

    if (BuildConfig.loadData) {
      try {
        createTestUser();

        if (BuildConfig.skipLogin) {
          mAuth.signInWithEmailAndPassword(BuildConfig.testUserEmail, BuildConfig.testUserPassword)
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
      } catch (Exception e) {
        Timber.e("Could not create test user: %s", e.getMessage());
      }
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
  public void createTestUser() throws Exception {
    Timber.i("Creating test user.");
    User testUser = new User(
            null,
            BuildConfig.testUserEmail,
            "test_user",
            BuildConfig.testUserPassword
    );
    testUser.setProfilePicUrl("https://images.unsplash.com/photo-1512327605305-64e5ce63b346?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");

    Timber.i("Creating test user in Firebase Auth.");
    mAuth.createUserWithEmailAndPassword(testUser.getEmail(), AESCrypt.encrypt(testUser.getPassword()))
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                AuthResult authResult = task.getResult();
                assert authResult != null;
                FirebaseUser firebaseUser = authResult.getUser();
                assert firebaseUser != null;

                testUser.setId(firebaseUser.getUid());
                UserRepository.getInstance().setOne(testUser);

                Timber.i("Created test user in Firebase Auth.");
                if (BuildConfig.loadData) insertData();
              } else {
                Timber.w("Failed to create test user in Firebase Auth");
              }
            });
  }

  private void insertData() {
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
              2, // initial delay
              BuildConfig.intervalLengthInSeconds,
              TimeUnit.SECONDS
      );
    }
  }
}