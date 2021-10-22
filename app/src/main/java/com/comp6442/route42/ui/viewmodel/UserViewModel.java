package com.comp6442.route42.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;

import timber.log.Timber;

// ViewModels are independent of configuration changes and are cleared when activity/fragment is destroyed
public class UserViewModel extends ViewModel {
  private final MutableLiveData<User> user = new MutableLiveData<>();

  //the ViewModel only exposes immutable LiveData objects to the observers.
  public LiveData<User> getUser() {
    return user;
  }

  public void setUser(User user) {
    if (user != null) this.user.setValue(user);
  }


  public ListenerRegistration addSnapshotListener(String uid) {
    if (uid != null) {
      DocumentReference docPath = UserRepository.getInstance().getOne(uid);
      return docPath.addSnapshotListener((value, error) -> {
        if (error == null) {
          assert value != null;
          setUser(value.toObject(User.class));
          Timber.i("added snapshot listener to uid: %s", uid);
          return;
        }
        Timber.e(error);
      });
    } else return null; // TODO fix, don't return null
  }


}