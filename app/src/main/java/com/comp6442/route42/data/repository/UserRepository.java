package com.comp6442.route42.data.repository;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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

  private UserRepository(FirebaseFirestore firestore) {
    super(firestore, "users", User.class);
  }

  public static UserRepository getInstance() {
    if (UserRepository.instance == null) {
      FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
      UserRepository.instance = new UserRepository(firestore);
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
      WriteBatch batch = this.firestore.batch();

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
      WriteBatch batch = this.firestore.batch();

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

  public void count() {
    collection.get().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        Timber.i("Total number of users: %d", Objects.requireNonNull(task.getResult()).size());
      } else {
        Timber.d("Could not get user count");
      }
    });
  }

//  public void follow(String uid, String followReceiverId) {
//    this.collection.document(uid).update("following", FieldValue.arrayUnion(followReceiverId));
//  }

//  TODO: not working
//  public void followAll(String uid) {
//    this.collection.get().onSuccessTask(success -> {
//      if (success != null) {
//        List<String> following = new ArrayList<>();
//
//        success.getDocuments().forEach(document -> {
//          if (document != null && document.get("uid") != null) following.add(String.format("users/%s", document.get("uid")));
//        });
//
//        this.collection.document(uid)
//                .update("following", FieldValue.arrayUnion(following))
//                .addOnSuccessListener(unused -> Timber.d("uid %s now follows all users", uid))
//                .addOnFailureListener(e -> {
//                  Timber.e("followAll failed for uid %s", uid);
//                });
//      }
//      return null;
//    });
//  }
}