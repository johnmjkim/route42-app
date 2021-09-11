package com.comp6442.route42.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;

import timber.log.Timber;

// ViewModels are independent of configuration changes and are cleared when activity/fragment is destroyed
public class UserViewModel extends ViewModel {
  // logged in user
  private final MutableLiveData<User> liveUser = new MutableLiveData<>();
  // user whose profile is loaded
  private MutableLiveData<User> profileUser = new MutableLiveData<>();

  @Override
  protected void onCleared() {
    super.onCleared();
  }

  public MutableLiveData<User> getLiveUser() {
    return liveUser;
  }

  public void setLiveUser(User user) {
    this.liveUser.setValue(user);
  }
  public void loadLiveUser (String uid) {
    UserRepository.getInstance().getOne(uid).get()
            .addOnSuccessListener(snapshot -> {
              User user = snapshot.toObject(User.class);
              setLiveUser(user);
              Timber.i("UserVM : loaded live user : %s", user);

            }).addOnFailureListener(Timber::e);

  }

  public LiveData<User> getProfileUser() {
    return profileUser;
  }
  private void setProfileUser(User user) {
    this.profileUser.setValue(user);
  }
  public void loadProfileUser (String uid) {
    UserRepository.getInstance().getOne(uid).get()
            .addOnSuccessListener(snapshot -> {
              User user = snapshot.toObject(User.class);
              setProfileUser(user);
              Timber.i("UserVM : loaded profile user : %s", user);
            }).addOnFailureListener(Timber::e);
  }

}