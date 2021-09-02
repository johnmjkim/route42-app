package com.comp6442.groupproject.data.repository;

import java.util.Objects;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import com.comp6442.groupproject.data.model.User;

public final class UserRepository extends FirestoreRepository<User> {
  private static final String TAG = "UserRepository";
  private static UserRepository instance = null;

  private UserRepository(FirebaseFirestore firestore) {
    super(firestore, "users", User.class);
  }

  public static UserRepository getInstance() {
    if (UserRepository.instance == null) {
      FirebaseFirestore firestore = FirebaseFirestore.getInstance();
      firestore.useEmulator("10.0.2.2", 8080);

      FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
              .setPersistenceEnabled(false)
              .build();

      firestore.setFirestoreSettings(settings);
      UserRepository.instance = new UserRepository(firestore);
    }
    return UserRepository.instance;
  }

  public void addUser(FirebaseUser firebaseUser) {
    // add user only if uid does not exist in user collection
    User user = new User(
            Objects.requireNonNull(firebaseUser.getUid()),
            Objects.requireNonNull(firebaseUser.getEmail())
    );
    this.collection.document(firebaseUser.getUid())
            .set(user)
            .addOnFailureListener(e -> Log.w(TAG, "Failed to add user: " + firebaseUser.getUid()));
  }

  public Task<QuerySnapshot> getUser(String uid) {
    return this.collection.whereEqualTo("uid", uid).get();
  }

  public void updateUser(User user) {
    collection.document(user.getUid()).set(user, SetOptions.merge());
  }
}