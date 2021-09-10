package com.comp6442.route42.data.repository;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.model.Model;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

import timber.log.Timber;

public abstract class FirestoreRepository<T extends Model> extends Repository<T> {
  protected static FirebaseFirestore firestore;
  protected CollectionReference collection;
  protected int batchSizeLimit = 500;

  public FirestoreRepository(String collectionPath, Class<T> cType) {
    this.classType = cType;

    if (firestore == null) {
      firestore = FirebaseFirestore.getInstance();

      if (BuildConfig.DEBUG) {
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
    }
    this.collection = firestore.collection(collectionPath);
  }

  abstract DocumentReference getOne(String id);

  abstract void setMany(List<T> items);
}
