package com.comp6442.groupproject.data.repository;

import com.comp6442.groupproject.BuildConfig;
import com.comp6442.groupproject.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

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
          Timber.w(exc);
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

  public DocumentReference getUser(String uid) {
    return this.collection.document(uid);
  }

  public void addUser(FirebaseUser firebaseUser) {
    // add user only if uid does not exist in user collection
    addUser(new User(
            Objects.requireNonNull(firebaseUser.getUid()),
            Objects.requireNonNull(firebaseUser.getEmail())
    ));
  }

  public void addUser(User user) {
    // add user only if uid does not exist in user collection
    this.collection.document(user.getUid())
            .set(user)
            .addOnFailureListener(Timber::e);
  }

  public void addUser(Map<String, Object> map) {
    // add user only if uid does not exist in user collection
    if (map.containsKey("uid"))
      this.collection.document((String) Objects.requireNonNull(map.get("uid")))
              .set(map)
              .addOnFailureListener(Timber::e);
  }

  public void updateUser(User user) {
    // create if not exists
    this.collection.document(user.getUid())
            .set(user, SetOptions.merge())
            .addOnFailureListener(Timber::e);
  }

  public void updateUser(Map<String, Object> map) {
    // create if not exists
    if (map.containsKey("uid"))
      this.collection.document((String) Objects.requireNonNull(map.get("uid")))
              .set(map, SetOptions.merge())
              .addOnFailureListener(Timber::e);
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