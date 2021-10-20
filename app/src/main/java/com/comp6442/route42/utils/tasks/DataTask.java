package com.comp6442.route42.utils.tasks;

import android.content.Context;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.Model;
import com.comp6442.route42.data.repository.FirestoreRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import timber.log.Timber;

public abstract class DataTask<T extends Model> implements Runnable {
  protected final FirebaseAuth mAuth;
  protected final String collectionName;
  protected final FirestoreRepository<T> repository;
  protected final int inputRawResourceId;
  protected final Class<T> classType;
  protected List<T> items;
  protected final Context demoContext;
  protected Gson gson;
  protected String jsonString;

  public DataTask(Context context, int inputRawResourceId, String collectionName, Class<T> classType, FirestoreRepository<T> repository, Gson gson) {
    this.mAuth = FirebaseAuthLiveData.getInstance().getAuth();
    this.demoContext = context;
    this.inputRawResourceId = inputRawResourceId;
    this.collectionName = collectionName;
    this.classType = classType;
    this.repository = repository;
    this.gson = gson;
    this.jsonString = readTextFile(demoContext.getResources().openRawResource(inputRawResourceId));
    this.items = gson.fromJson(jsonString, (Type) TypeToken.getParameterized(List.class, classType).getType());
    Timber.i("Initialized task to insert documents into collection: %s", collectionName);
  }

  @Override
  public void run() {
    if (this.mAuth.getCurrentUser() == null) {
      this.mAuth.signInWithEmailAndPassword(
              BuildConfig.testUserEmail,
              BuildConfig.testUserPassword
      );
    }
    if (this.mAuth.getCurrentUser() == null) Timber.w("Not signed in");
    else {
      createDocuments();
    }
  }

  protected String readTextFile(InputStream inputStream) {
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


  protected void createDocuments() {
    this.repository.setMany(items);
    Timber.i("Created %d %s", items.size(), collectionName);
  }
}