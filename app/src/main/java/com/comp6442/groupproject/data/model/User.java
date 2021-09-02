package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
  @NonNull
  private final String uid;

  private String userName = null;

  @NonNull
  private String email;

  public User(@NonNull String uid, @NonNull String email) {
    this.uid = uid;
    this.email = email;
  }

  @NonNull
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

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setEmail(@NonNull String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return String.format("User{uid='%s', userName='%s', email='%s'}", uid, userName, email);
  }
}
