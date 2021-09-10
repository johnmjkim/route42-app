package com.comp6442.route42.data.repository;

import com.comp6442.route42.data.model.Model;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class FirestoreRepository<T extends Model> extends Repository<T> {
  protected FirebaseFirestore firestore;
  protected CollectionReference collection;
  protected int batchSizeLimit = 500;

  public FirestoreRepository(FirebaseFirestore firestore, String collectionPath, Class<T> cType) {
    this.classType = cType;
    this.firestore = firestore;
    this.collection = firestore.collection(collectionPath);
  }

  abstract DocumentReference getOne(String id);
}
