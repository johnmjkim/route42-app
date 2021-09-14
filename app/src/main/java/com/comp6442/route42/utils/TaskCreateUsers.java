package com.comp6442.route42.utils;

import android.content.Context;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.R;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class TaskCreateUsers extends DataTask {
  private final List<User> userList;

  public TaskCreateUsers(Context context, boolean debug) {
    super(debug, false, "users", UserRepository.getInstance(), User.class, context);
    gson = UserRepository.getJsonDeserializer();
    jsonString = readTextFile(demoContext.getResources().openRawResource(R.raw.users));
    userList = Arrays.asList(gson.fromJson(jsonString, (Type) User[].class));
  }

  @Override
  public void run() {
    if (this.mAuth.getCurrentUser() == null) {
      Timber.i("Signed in as test user");
      this.mAuth.signInWithEmailAndPassword(BuildConfig.testUserEmail, BuildConfig.testUserPassword);
    }
    if (this.mAuth.getCurrentUser() == null) Timber.w("not signed in");
    else {
      createUsers();
      Timber.d("Task completed.");
    }
  }

  private void createUsers() {
    UserRepository.getInstance().setMany(userList);
    Timber.i("Created users.");
  }
}
