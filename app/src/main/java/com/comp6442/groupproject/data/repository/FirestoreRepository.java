package com.comp6442.groupproject.data.repository;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Optional;

import timber.log.Timber;

public class FirestoreRepository<T> implements IRepository<T> {
  private final Class<T> classType;
  protected CollectionReference collection;
  protected FirebaseFirestore firestore;

  public FirestoreRepository(FirebaseFirestore firestore, String collectionPath, Class<T> classType) {
    this.firestore = firestore;
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
      Timber.w(ex);
      return Optional.empty();
    }
  }
}
