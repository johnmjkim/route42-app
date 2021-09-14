package com.comp6442.route42.data;

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
public class UserViewModel extends ViewModel {
  // logged in user
  private final MutableLiveData<User> liveUser = new MutableLiveData<>();
  // user whose profile is loaded
  private final MutableLiveData<User> profileUser = new MutableLiveData<>();

  //the ViewModel only exposes immutable LiveData objects to the observers.
  public LiveData<User> getLiveUser() {
    return liveUser;
  }

  public void setLiveUser(User user) {
    this.liveUser.setValue(user);
  }

  public void loadLiveUser(String uid) {
    Task<DocumentSnapshot> task = UserRepository.getInstance().getOne(uid).get();
    task.addOnSuccessListener(snapshot -> {
      String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
      User user = snapshot.toObject(User.class);

      // only react to server-side changes in the document snapshot
      if (source.equals("Server")) {
        setLiveUser(user);
        Timber.i("UserVM : loaded profile user : %s", user);
      } else {
        Timber.w("Snapshot Change observed: Source=%s data=%s", source, user);
      }
    }).addOnFailureListener(Timber::e);
  }

  /**
   * syncs this viewModel data with corresponding Firebase document
   */
  public ListenerRegistration addSnapshotListenerToProfileUser(String uid) {
    DocumentReference docPath = UserRepository.getInstance().getOne(uid);
    return docPath.addSnapshotListener((value, error) -> {
      if (error == null) {
        setProfileUser(value.toObject(User.class));
        Timber.i("added snapshot listener to uid: %s", uid);
        return;
      }
      Timber.e(error);
    });
  }

  public ListenerRegistration addSnapshotListenerToLiveUser(String uid) {
    if (uid != null) {
      DocumentReference docPath = UserRepository.getInstance().getOne(uid);
      return docPath.addSnapshotListener((value, error) -> {
        if (error == null) {
          setLiveUser(value.toObject(User.class));
          Timber.i("added snapshot listener to uid: %s", uid);
          return;
        }
        Timber.e(error);
      });
    } else return null; // TODO fix, don't return null
  }


  public LiveData<User> getProfileUser() {
    return profileUser;
  }

  private void setProfileUser(User user) {
    this.profileUser.setValue(user);
  }

  public void loadProfileUser(String uid) {
    UserRepository.getInstance().getOne(uid).get()
            .addOnSuccessListener(snapshot -> {
              User user = snapshot.toObject(User.class);
              setProfileUser(user);
              Timber.i("UserVM : loaded profile user : %s", user);
            }).addOnFailureListener(Timber::e);
  }
}