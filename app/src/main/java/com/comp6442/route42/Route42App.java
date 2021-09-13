package com.comp6442.route42;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.model.UserLike;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserLikeRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.activity.LogInActivity;
import com.comp6442.route42.ui.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class Route42App extends Application {
  private FirebaseAuth mAuth;
  //  In many apps, there's no need to work with an application class directly. However, there are a few acceptable uses of a custom application class:
  //
  //  Specialized tasks that need to run before the creation of your first activity
  //  Global initialization that needs to be shared across all components (crash reporting, persistence)
  //  Static methods for easy access to static immutable data such as a shared network client object
  //  Note that you should never store mutable shared data inside the Application object since that data might disappear or become invalid at any time. Instead, store any mutable shared data using persistence strategies such as files, SharedPreferences or SQLite.

  // Called when the application is starting, before any other application objects have been created.
  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onCreate() {
    super.onCreate();

    // initialize Timber logger in application class
    Timber.plant(new CustomLogger());

    mAuth = FirebaseAuthLiveData.getInstance().getAuth();

    if (BuildConfig.DEBUG) {
      Timber.i("Application starting on DEBUG mode");
    } else {
      Timber.i("Application starting");
    }

    if (BuildConfig.loadData) {
      Timber.i("Loading sample data");
      createFakeUsers();
      createTestUser();
      createFakePosts();
      Timber.i("Loaded sample data");
    }
    if(BuildConfig.skipLogin) {
      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);

    } else{
      // sign out and take user to log in screen
      if (mAuth.getCurrentUser() != null) mAuth.signOut();
      Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }

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
  }

  public void createTestUser() {
    Timber.i("Creating test user.");

    // create test user and add to firebase and firestore
    User testUser = new User(null, BuildConfig.testUserEmail, "test_user", BuildConfig.testUserPassword);
    mAuth.createUserWithEmailAndPassword(testUser.getEmail(), testUser.getPassword())
          .addOnSuccessListener(authResult -> {
                FirebaseUser firebaseUser = authResult.getUser();

                if (firebaseUser != null) {
                  testUser.setId(firebaseUser.getUid());
                  UserRepository.getInstance().setOne(testUser);
                  Timber.i("Created test user.");
                  mAuth.signOut();
                }
          }).addOnFailureListener(error -> {
            Timber.w("Could not create test_user in Firebase Auth");
            // Timber.e(error);
          });
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void createFakeUsers() {
    Timber.i("Creating fake users.");
    InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.users);
    String jsonString = readTextFile(inputStream);

    Gson gson = UserRepository.getJsonDeserializer();
    List<User> usersList = Arrays.asList(gson.fromJson(jsonString, (Type) User[].class));
    UserRepository.getInstance().setMany(usersList);
    Timber.i("Created fake users.");
  }

  public String readTextFile(InputStream inputStream) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    byte[] buf = new byte[1024];
    int len;
    try {
      while ((len = inputStream.read(buf)) != -1) outputStream.write(buf, 0, len);
      outputStream.close();
      inputStream.close();
    } catch (IOException e) {
      Timber.w(e);
    }
    return outputStream.toString();
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public void createFakePosts() {
    Timber.i("Creating fake posts.");
    if (mAuth.getCurrentUser() == null)
      mAuth.signInWithEmailAndPassword(BuildConfig.testUserEmail, BuildConfig.testUserPassword);

    InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.posts);
    String jsonString = readTextFile(inputStream);

    Gson gson = PostRepository.getJsonDeserializer();
    List<Post> posts = Arrays.asList(gson.fromJson(jsonString, (Type) Post[].class));
    PostRepository.getInstance().createMany(posts);
    Timber.i("Created fake posts.");

    InputStream inputStream2 = getApplicationContext().getResources().openRawResource(R.raw.user_likes);
    List<UserLike> userLikesList = UserLikeRepository.getInstance().deserializeJSON(inputStream2);
    UserLikeRepository.getInstance().createMany(userLikesList);
    Timber.i("Created fake UserLike on posts");
  }
}