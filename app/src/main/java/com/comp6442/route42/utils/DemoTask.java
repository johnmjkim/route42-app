package com.comp6442.route42.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class DemoTask implements Runnable {
  private final boolean DEBUG;
  private final boolean DEMO;
  private final FirebaseAuth mAuth;
  private final String collectionName;
  private final FirebaseFirestore firestore;
  private final CollectionReference collection;
  private final Context demoContext;
  private int idx = 0;
  private Gson gson;
  private String jsonString;
  private List<User> userList;
  private List<Post> postList;

  public DemoTask(Context context, String collectionName, boolean debug) {
    this(context, collectionName, debug, false);
  }

  public DemoTask(Context context, String collectionName, boolean debug, boolean demo) {
    this.demoContext = context;
    this.collectionName = collectionName;
    this.DEBUG = debug;
    this.DEMO = demo;
    this.mAuth = FirebaseAuthLiveData.getInstance().getAuth();
    this.firestore = FirebaseFirestore.getInstance();

    if (DEBUG) {
      try {
        firestore.useEmulator("10.0.2.2", 8080);
      } catch (IllegalStateException exc) {
        Timber.d(exc);
      }
    }

    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build();
    firestore.setFirestoreSettings(settings);
    this.collection = firestore.collection(this.collectionName);


    switch (collectionName) {
      case "users":
        gson = UserRepository.getJsonDeserializer();
        jsonString = readTextFile(demoContext.getResources().openRawResource(R.raw.users));
        userList = Arrays.asList(gson.fromJson(jsonString, (Type) User[].class));
        break;
      case "posts":
        Timber.i("Creating posts.");
        gson = PostRepository.getJsonDeserializer();
        jsonString = readTextFile(
                demoContext.getResources().openRawResource(R.raw.posts)
        );
        postList = Arrays.asList(gson.fromJson(jsonString, (Type) Post[].class));
        Collections.shuffle(postList);
        break;
    }

    Timber.i("Initialized demo task with collection: %s", collectionName);
  }

  @Override
  public void run() {
    Timber.i("Starting task with parameters: collection=%s DEBUG=%s DEMO=%s", collectionName, BuildConfig.DEBUG, BuildConfig.DEMO);

    this.mAuth.signInWithEmailAndPassword(BuildConfig.testUserEmail, BuildConfig.testUserPassword)
            .addOnFailureListener(error -> {
              Timber.w("Could not sign in as test user");
              Timber.e(error);
            }).addOnSuccessListener(unused -> {
              Timber.i("Signed in as test user");

              switch (collectionName) {
                case "users":
                  createUsers();
                  break;
                case "posts":
                  createPosts();
                  break;
              }
              Timber.i("Task completed.");
            }
    );
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void createUsers() {
    Timber.i("Creating users.");
    UserRepository.getInstance().setMany(userList);
    Timber.i("Created users.");
  }

  private void createPosts(){
    Timber.i("Creating posts.");

    if (!DEMO) PostRepository.getInstance().createMany(postList);
    else {
      createPostsMiniBatch(
              BuildConfig.batchSize,
              Math.max(postList.size(), BuildConfig.demoPostLimit)
      );
    }
    Timber.i("Created posts.");
  }

  public void createPostsMiniBatch(int batchSize, int limit) {
    // Get a new write batch
    WriteBatch livePosts = this.firestore.batch();

    // insert n posts (buffered)
    int prevIdx = idx;
    while (idx < limit && idx - prevIdx < batchSize) {
      Post post = postList.get(idx);
      DocumentReference postRef = this.collection.document(post.getId());
      livePosts.set(postRef, post);

      Timber.d("%d : %s", idx, post);
      idx++;
    }

    // Commit the batch
    livePosts.commit()
            .addOnFailureListener(Timber::e)
            .addOnSuccessListener(unused -> Timber.i("Demo Batch write complete: %d posts", batchSize));
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
}


