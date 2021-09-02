package com.comp6442.groupproject.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User extends Model {
  public static final String FIELD_FIREBASE_UID = "uid";
  public static final String FIELD_USERNAME = "username";
  public static final String FIELD_EMAIL = "email";

  private String uid;
  private String userName;
  private String email;

  public User(String userName, String email) {
    this.userName = userName;
    this.email = email;
  }

  public String getUid() {
    return uid;
  }

  public String getUserName() {
    return userName;
  }

  public String getEmail() {
    return email;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
}