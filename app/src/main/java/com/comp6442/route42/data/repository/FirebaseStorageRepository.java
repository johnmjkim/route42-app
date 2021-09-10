package com.comp6442.route42.data.repository;

import com.comp6442.route42.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import timber.log.Timber;

public class FirebaseStorageRepository {
  private static FirebaseStorageRepository instance = null;
  private static FirebaseStorage storage = null;
  private static String bucketUrl = null;
  public static final long BUFFER_SIZE = 1024 * 1024 * 5; // 5MB

  private FirebaseStorageRepository(FirebaseStorage storage) {
    FirebaseStorageRepository.storage = storage;
  }

  public static FirebaseStorageRepository getInstance() {
    if (FirebaseStorageRepository.instance == null) {
      FirebaseStorage storage = FirebaseStorage.getInstance();
      if (BuildConfig.DEBUG) {
        try {
          storage.useEmulator("10.0.2.2", 9199);
        } catch (IllegalStateException exc) {
          Timber.d(exc);
        }
      }
      FirebaseStorageRepository.instance = new FirebaseStorageRepository(storage);
      FirebaseStorageRepository.bucketUrl = FirebaseApp.getInstance().getOptions().getStorageBucket();
    }
    return FirebaseStorageRepository.instance;
  }

  public StorageReference get(String path) {
    String url = String.format("gs://%s/%s", bucketUrl, path);
    Timber.i(url);
    StorageReference storageRef = storage.getReferenceFromUrl(url);
    return storageRef;
  }
}
