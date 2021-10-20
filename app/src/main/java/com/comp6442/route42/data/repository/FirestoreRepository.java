package com.comp6442.route42.data.repository;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.model.Model;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import timber.log.Timber;

public abstract class FirestoreRepository<T extends Model> extends Repository<T> {
  protected static FirebaseFirestore firestore;
  protected CollectionReference collection;
  protected String collectionName;
  protected static final int FIREBASE_FIRESTORE_BATCH_LIMIT = 500;

  public FirestoreRepository(String collectionName, Class<T> cType) {
    this.classType = cType;

    if (firestore == null) {
      firestore = FirebaseFirestore.getInstance();

      if (BuildConfig.EMULATOR) {
        try {
          firestore.useEmulator(BuildConfig.EMULATOR_ADDRESS, BuildConfig.FIREBASE_FIRESTORE_PORT);
        } catch (IllegalStateException exc) {
          Timber.d(exc);
        }
      }

      FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
              .setPersistenceEnabled(false)
              .build();

      firestore.setFirestoreSettings(settings);
    }

    this.collection = firestore.collection(collectionName);
    this.collectionName = collectionName;
  }

  public DocumentReference getOne(String uid) {
    return this.collection.document(uid);
  }

  public void createOne(T item) {
    // add only if id does not exist in collection
    this.collection.document(item.getId())
            .set(item)
            .addOnSuccessListener(unused -> Timber.i("Write complete: %s", item.toString()))
            .addOnFailureListener(Timber::e);
  }

  public void createMany(List<T> items) {
    int idx = 0;
    while (idx < items.size()) {
      int counter = 0;
      WriteBatch batch = firestore.batch();

      while (counter < FIREBASE_FIRESTORE_BATCH_LIMIT && idx < items.size()) {
        T item = items.get(idx);
        DocumentReference postRef = this.collection.document(item.getId());
        batch.set(postRef, item);
        counter++;
        idx++;
      }

      // Commit the batch
      int finalIdx = idx;
      batch.commit()
              .addOnSuccessListener(unused -> Timber.i("Batch write complete: %s %d items", this.collectionName, finalIdx))
              .addOnFailureListener(Timber::e);
    }
  }

  public void setOne(T item) {
    // create if not exists
    this.collection.document(item.getId())
            .set(item, SetOptions.merge())
            .addOnFailureListener(Timber::e);
  }

  public void setMany(List<T> items) {
    // create if not exists
    int idx = 0;
    while (idx < items.size()) {
      int counter = 0;
      // Get a new write batch
      WriteBatch batch = firestore.batch();

      while (counter < FIREBASE_FIRESTORE_BATCH_LIMIT && idx < items.size()) {
        T item = items.get(idx);
        DocumentReference ref = this.collection.document(item.getId());
        batch.set(ref, item, SetOptions.merge());
        counter++;
        idx++;
      }

      // Commit the batch
      int finalIdx = idx;
      batch.commit()
              .addOnSuccessListener(unused -> Timber.i("Batch set complete: %s %d items", this.collectionName, finalIdx))
              .addOnFailureListener(Timber::e);
    }
  }
}
