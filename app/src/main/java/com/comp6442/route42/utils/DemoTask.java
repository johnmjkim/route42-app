package com.comp6442.route42.utils;

import android.content.Context;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class DemoTask implements Runnable {
  private final boolean DEBUG;
  private final boolean DEMO;
  private final FirebaseAuth mAuth;
  private final String collectionName;
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

    switch (collectionName) {
      case "users":
        gson = UserRepository.getJsonDeserializer();
        jsonString = readTextFile(demoContext.getResources().openRawResource(R.raw.users));
        userList = Arrays.asList(gson.fromJson(jsonString, (Type) User[].class));
        break;
      case "posts":
        gson = PostRepository.getJsonDeserializer();
        jsonString = readTextFile(
                demoContext.getResources().openRawResource(R.raw.posts)
        );
        postList = Arrays.asList(gson.fromJson(jsonString, (Type) Post[].class));
        Collections.shuffle(postList);
        break;
    }

    Timber.i("Initialized demo task with collection: %s parameters: DEBUG=%s DEMO=%s",
            collectionName,
            BuildConfig.DEBUG,
            BuildConfig.DEMO);
  }

  @Override
  public void run() {
    if (this.mAuth.getCurrentUser() == null) {
      Timber.i("Signed in as test user");
      this.mAuth.signInWithEmailAndPassword(BuildConfig.testUserEmail, BuildConfig.testUserPassword);
    }
    if (this.mAuth.getCurrentUser() == null) Timber.w("not signed in");
    else {
      switch (collectionName) {
        case "users":
          createUsers();
          break;
        case "posts":
          createPosts();
          break;
      }
      Timber.d("Task completed.");
    }
  }

  private void createUsers() {
    UserRepository.getInstance().setMany(userList);
    Timber.i("Created users.");
  }

  private void createPosts() {
    if (!DEMO) PostRepository.getInstance().createMany(postList);
    else {
      createPostsMiniBatch(
              BuildConfig.batchSize,
              Math.min(postList.size(), BuildConfig.demoPostLimit)
      );
    }
    Timber.i("Created posts.");
  }

  public void createPostsMiniBatch(int batchSize, int limit) {
    Timber.i("Creating posts %d - %d", idx, Math.min(postList.size(), idx + BuildConfig.batchSize));

    int prevIdx = idx;
    idx += batchSize;
    if (idx + batchSize < limit) {
      PostRepository.getInstance().setMany(postList.subList(prevIdx, idx));
    } else {
      Thread.currentThread().interrupt();
      Timber.i("Completed demo.");
    }
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


