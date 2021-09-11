package com.comp6442.route42.data.repository;

import com.comp6442.route42.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public final class UserRepository extends FirestoreRepository<User> {
  private static UserRepository instance = null;

  private UserRepository() {
    super("users", User.class);
  }
  public CollectionReference getCollectionRef() { return this.collection; }
  public static UserRepository getInstance() {
    if (UserRepository.instance == null) {
      UserRepository.instance = new UserRepository();
    }
    return UserRepository.instance;
  }

  public static Gson getJsonDeserializer() {
    return new GsonBuilder().registerTypeAdapter(DocumentReference.class, (JsonDeserializer<DocumentReference>) (json, type, context) -> {
      String str = json.toString();
      return UserRepository.getInstance().getOne(str);
    }).create();
  }

  public DocumentReference getOne(String uid) {
    return this.collection.document(uid);
  }

  public void createOne(FirebaseUser firebaseUser) {
    // add user only if uid does not exist in user collection
    createOne(new User(
            Objects.requireNonNull(firebaseUser.getUid()),
            Objects.requireNonNull(firebaseUser.getEmail())
    ));
  }

  public void createOne(User user) {
    // add user only if uid does not exist in user collection
    this.collection.document(user.getId())
            .set(user)
            .addOnFailureListener(Timber::e);
  }

  public void createOne(Map<String, Object> map) {
    // add user only if uid does not exist in user collection
    if (map.containsKey("uid"))
      this.collection.document((String) Objects.requireNonNull(map.get("uid")))
              .set(map)
              .addOnFailureListener(Timber::e);
  }

  public void createMany(List<User> users) {
    // create if not exists
    // batch size limit is 500 documents
    int idx = 0;
    while (idx < users.size()) {
      int counter = 0;
      // Get a new write batch
      WriteBatch batch = firestore.batch();

      while (counter < super.batchSizeLimit && idx < users.size()) {
        User user = users.get(idx);
        DocumentReference ref = this.collection.document(user.getId());
        batch.set(ref, user, SetOptions.merge());
        counter++;
        idx++;
      }
      // Commit the batch
      batch.commit().addOnFailureListener(Timber::e)
              .addOnSuccessListener(task -> Timber.i("Batch write complete: users"));
    }
  }

  public void setOne(User user) {
    // create if not exists
    this.collection.document(user.getId())
            .set(user, SetOptions.merge())
            .addOnFailureListener(Timber::e);
  }

  public void setOne(Map<String, Object> map) {
    // create if not exists
    if (map.containsKey("uid"))
      this.collection.document((String) Objects.requireNonNull(map.get("uid")))
              .set(map, SetOptions.merge())
              .addOnFailureListener(Timber::e);
  }

  public void setMany(List<User> users) {
    // create if not exists
    // batch size limit is 500 documents
    int idx = 0;
    while (idx < users.size()) {
      int counter = 0;
      // Get a new write batch
      WriteBatch batch = firestore.batch();

      while (counter < super.batchSizeLimit && idx < users.size()) {
        User user = users.get(idx);
        DocumentReference ref = this.collection.document(user.getId());
        batch.set(ref, user, SetOptions.merge());
        counter++;
        idx++;
      }
      // Commit the batch
      batch.commit().addOnFailureListener(Timber::e)
              .addOnSuccessListener(task -> Timber.i("Batch write complete: users"));
    }
  }

  public void follow(String followGiverId, String followReceiverId) {
    DocumentReference followGiver = this.collection.document(followGiverId);
    DocumentReference followReceiver = this.collection.document(followReceiverId);
    followGiver.update("following", FieldValue.arrayUnion(followReceiver));
    followReceiver.update("followers", FieldValue.arrayUnion(followGiver));
  }

  public void count() {
    collection.get().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        Timber.i("Total number of users: %d", Objects.requireNonNull(task.getResult()).size());
      } else {
        Timber.d("Could not get user count");
      }
    });
  }
}