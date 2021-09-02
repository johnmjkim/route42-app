package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
  private String uid;

  private String userName;

  @NonNull
  private String email;

  public User(@NonNull String email) {
    this.email = email;
  }

  public String getUid() {
    return uid;
  }

  public String getUserName() {
    return userName;
  }

  @NonNull
  public String getEmail() {
    return email;
  }

  public void setUid(@NonNull String uid) {
    this.uid = uid;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setEmail(@NonNull String email) {
    this.email = email;
  }
}
