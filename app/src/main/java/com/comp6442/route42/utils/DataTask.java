package com.comp6442.route42.utils;

import android.content.Context;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.Model;
import com.comp6442.route42.data.repository.Repository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public abstract class DataTask<T extends Model> implements Runnable {
  protected final FirebaseAuth mAuth;
  protected final boolean DEBUG;
  protected final boolean DEMO;
  protected final String collectionName;
  protected final Repository<T> repository;
  protected final Class<T> classType;
  protected final Context demoContext;
  protected Gson gson;
  protected String jsonString;

  public DataTask(boolean debug, boolean demo, String collectionName, Repository<T> repository, Class<T> classType, Context context) {
    this.mAuth = FirebaseAuthLiveData.getInstance().getAuth();
    this.DEBUG = debug;
    this.DEMO = demo;
    this.collectionName = collectionName;
    this.repository = repository;
    this.classType = classType;
    this.demoContext = context;

    Timber.i("Initialized demo task with collection: %s parameters: DEBUG=%s DEMO=%s",
            collectionName,
            BuildConfig.DEBUG,
            BuildConfig.DEMO);
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