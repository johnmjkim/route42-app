package com.comp6442.groupproject;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.comp6442.groupproject.data.model.Post;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.PostRepository;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class App42 extends Application {
  private FirebaseAuth mAuth;
  //  In many apps, there's no need to work with an application class directly. However, there are a few acceptable uses of a custom application class:
  //
  //  Specialized tasks that need to run before the creation of your first activity
  //  Global initialization that needs to be shared across all components (crash reporting, persistence)
  //  Static methods for easy access to static immutable data such as a shared network client object
  //  Note that you should never store mutable shared data inside the Application object since that data might disappear or become invalid at any time. Instead, store any mutable shared data using persistence strategies such as files, SharedPreferences or SQLite.

  // Called when the application is starting, before any other application objects have been created.
  // Overriding this method is totally optional!
  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onCreate() {
    super.onCreate();

    // initialize timber in application class
    Timber.plant(new Timber.DebugTree());

    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance();

    if (BuildConfig.DEBUG) {

      try {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        mAuth.useEmulator("10.0.2.2", 9099);
      } catch (IllegalStateException exc) {
        Timber.w(exc);
      }

      createTestUser();
      createFakePosts();
      createFakeUsers();
      mAuth.signOut();
      Toast.makeText(App42.this, "Data loaded.", Toast.LENGTH_SHORT).show();
      Timber.i("Application starting on DEBUG mode");
    } else {
      Timber.i("Application starting");
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

  public void createTestUser() {
    // add test user to firebase and firestore

    User testUser = new User(null, "foo@bar.com", "test_user", "password");
    mAuth.createUserWithEmailAndPassword(testUser.getEmail(), testUser.getPassword())
            .addOnCompleteListener(
                    task -> {
                      if (task.isSuccessful()) Timber.d("Created test user.");
                    }
            );
    mAuth.signInWithEmailAndPassword(testUser.getEmail(), testUser.getPassword()).addOnCompleteListener(task -> {
      FirebaseUser firebaseUser = Objects.requireNonNull(task.getResult()).getUser();
      if (firebaseUser != null) {
        UserRepository.getInstance().addUser(firebaseUser);
        UserRepository.getInstance().updateUser(
                testUser.setUid(firebaseUser.getUid())
        );
        mAuth.signOut();
        Timber.d("Insert to Firestore complete: test user");
      }
    });
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void createFakeUsers() {
    InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.users);
    String jsonString = readTextFile(inputStream);
    List<User> usersList = Arrays.asList(new Gson().fromJson(jsonString, (Type) User[].class));
    usersList.forEach(user -> UserRepository.getInstance().updateUser(user));
    UserRepository.getInstance().count();
    Timber.d("Insert to Firestore complete: fake users");
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
    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, type, context) -> {
      String tsString = json.toString();
      Integer decimalIdx = (tsString.contains(".")) ? tsString.indexOf(".") : tsString.length();
      return new Timestamp(
              Long.parseLong(tsString.substring(0, decimalIdx)),
              (decimalIdx != tsString.length()) ? Integer.parseInt(tsString.substring(decimalIdx + 1)) : 0
      );
    }).registerTypeAdapter(Double.class, (JsonDeserializer<Double>) (json, type, context) -> {
      return json.getAsDouble();
    }).create();

    InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.posts);
    String jsonString = readTextFile(inputStream);
    List<Post> posts = Arrays.asList(gson.fromJson(jsonString, (Type) Post[].class));
    PostRepository.getInstance().addPosts(posts);
    Timber.d("Insert to Firestore complete: fake posts");
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    mAuth.signOut();
  }
}