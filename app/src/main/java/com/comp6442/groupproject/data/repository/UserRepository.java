package com.comp6442.groupproject.data.repository;

import java.util.Objects;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

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
    User user = new User(Objects.requireNonNull(firebaseUser.getEmail()));
    user.setUid(firebaseUser.getUid());
    this.collection.add(user)
            .addOnSuccessListener(
                    documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
  }

  public Task<DocumentSnapshot> getUser(String uid) {
    return this.collection.document(uid).get();
  }
}