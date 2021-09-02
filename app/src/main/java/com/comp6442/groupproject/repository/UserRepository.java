package com.comp6442.groupproject.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.comp6442.groupproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public final class UserRepository extends FirestoreRepository {
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

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void addUser(FirebaseUser firebaseUser) {
    User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail());
    user.setUid(firebaseUser.getUid());
    this.collection.add(user)
            .addOnSuccessListener(
                    documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

//    // Create a new user
//    Map<String, Object> user = new HashMap<>();
//    user.put("uid", "Ada");
//    user.put("username", "Lovelace");
//
//    // Add a new document with a generated ID
//    firestore.collection("users")
//            .add(user)
//            .addOnSuccessListener(
//                    documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
//            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
  }

  public MutableLiveData<User> getUser(String uid) {
    MutableLiveData<User> user = new MutableLiveData<>();

    this.collection.document(uid).get().addOnCompleteListener(
            (OnCompleteListener<DocumentSnapshot>) task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot documentSnapshot = task.getResult();
        if (documentSnapshot != null) {
          user.setValue(documentSnapshot.toObject(User.class));
          // TODO
          // mListener will be called from here
          // or
          // set the value of the liveData here
          // to send the object v obtained
        }
      }
    });
    return user;
  }
}
