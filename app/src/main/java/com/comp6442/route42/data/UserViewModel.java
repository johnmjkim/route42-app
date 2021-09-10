package com.comp6442.route42.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.User;

// ViewModels are independent of configuration changes and are cleared when activity/fragment is destroyed
public class UserViewModel extends ViewModel {

  private final MutableLiveData<User> liveUser = new MutableLiveData<>();

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
}