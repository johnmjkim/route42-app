package com.comp6442.route42.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import timber.log.Timber;

// ViewModels are independent of configuration changes and are cleared when activity/fragment is destroyed
public class ProfileUserViewModel extends UserViewModel {

  public void loadProfileUser(String uid) {
    Task<DocumentSnapshot> task = UserRepository.getInstance().getOne(uid).get();
    task.addOnSuccessListener(snapshot -> {
      String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
      User user = snapshot.toObject(User.class);

      // only react to server-side changes in the document snapshot
      if (source.equals("Server")) {
        setUser(user);
        Timber.i("UserVM : loaded profile user : %s", user);
      } else {
        Timber.w("Snapshot Change observed: Source=%s data=%s", source, user);
      }
    }).addOnFailureListener(Timber::e);
  }


}