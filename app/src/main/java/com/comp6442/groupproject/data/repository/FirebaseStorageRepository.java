package com.comp6442.groupproject.data.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.comp6442.groupproject.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import timber.log.Timber;

public class FirebaseStorageRepository {
  private static FirebaseStorageRepository instance = null;
  private static FirebaseStorage storage = null;

  private FirebaseStorageRepository(FirebaseStorage storage) {
    FirebaseStorageRepository.storage = storage;
  }

  public static FirebaseStorageRepository getInstance() {
    if (FirebaseStorageRepository.instance == null && BuildConfig.DEBUG) {
      try {
        FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
        FirebaseStorageRepository.instance = new FirebaseStorageRepository(FirebaseStorage.getInstance());
      } catch (IllegalStateException exc) {
        Timber.d(exc);
      }
    }
    return FirebaseStorageRepository.instance;
  }

  public StorageReference get(String path) {
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    // Create a reference with an initial file path and name
    StorageReference pathReference = storageRef.child(path);
    return pathReference;
  }
}
