package com.comp6442.groupproject.data.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Optional;

public class FirestoreRepository<T> implements IRepository<T> {
  private static final String TAG = "FirestoreRepository";
  protected CollectionReference collection;
  private final Class<T> classType;

  public FirestoreRepository(FirebaseFirestore firestore, String collectionPath, Class<T> classType) {
    this.collection = firestore.collection(collectionPath);
    this.classType = classType;
  }

  public String add(T data) {
    // Create empty doc and add data
    DocumentReference newRef = collection.document();
    newRef.set(data);
    return newRef.getId();
  }

  @RequiresApi(api = Build.VERSION_CODES.N) // required for use of Optional
  public Optional<T> getById(String id) {
    try {
      Task<DocumentSnapshot> futureEntry = collection.document(id).get();
      DocumentSnapshot entry = futureEntry.getResult();
      return Optional.ofNullable(entry.toObject(classType));
    } catch (Exception ex) {
      Log.d(TAG, ex.getMessage());
      return Optional.empty();
    }
  }
}
