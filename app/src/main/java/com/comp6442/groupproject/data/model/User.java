package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@IgnoreExtraProperties
public class User {
  private final String uid;

  @NonNull
  private final String email;

  @NonNull
  private final String userName;

  @Exclude
  private String password = null;

  private List<String> following;
  private List<String> followers;

  public User(@NonNull String email, @NonNull String userName) {
    this.uid = UUID.randomUUID().toString();
    this.email = email;
    this.userName = userName;
  }

  public User(String uid, @NonNull String email, @NonNull String userName) {
    this.uid = uid;
    this.email = email;
    this.userName = userName;
  }

  public User(String uid, @NonNull String email, @NonNull String userName, String password) {
    this.uid = uid;
    this.email = email;
    this.userName = userName;
    this.password = password;
  }

  public User(String uid, @NonNull String email, @NonNull String userName, List<String> following, List<String> followers) {
    this.uid = uid;
    this.email = email;
    this.userName = userName;
    this.following = following;
    this.followers = followers;
  }

  public User(String uid, @NonNull String email, @NonNull String userName, List<String> following, List<String> followers, String password) {
    this.uid = uid;
    this.email = email;
    this.userName = userName;
    this.following = following;
    this.followers = followers;
    this.password = password;
  }

  public static User fromDocumentSnapshot(DocumentSnapshot snapshot) {
    Map<String, Object> data = snapshot.getData();

    User user = new User(
            (String) Objects.requireNonNull(data.get("uid")),
            (String) Objects.requireNonNull(data.get("email")),
            (String) Objects.requireNonNull(data.get("userName"))
    );

    if (data.containsKey("following")) user.setFollowing((List<String>) data.get("following"));
    if (data.containsKey("followers")) user.setFollowers((List<String>) data.get("followers"));
    return user;
  }

  @NonNull
  @Override
  public String toString() {
    return "User{" +
            "uid='" + uid + '\'' +
            ", userName='" + userName + '\'' +
            ", email='" + email + '\'' +
            ", following=" + following +
            ", followers=" + followers +
            '}';
  }

  public String getPassword() {
    return password;
  }

  public String getUid() {
    return uid;
  }

  public User setUid(String uid) {
    return new User(
            uid,
            this.getEmail(),
            this.getUserName(),
            this.getFollowing(),
            this.getFollowers(),
            this.getPassword()
    );
  }

  @NonNull
  public String getEmail() {
    return email;
  }

  public User setEmail(@NonNull String email) {
    return new User(
            this.getUid(),
            email,
            this.getUserName(),
            this.getFollowing(),
            this.getFollowers(),
            this.getPassword()
    );
  }

  @NonNull
  public String getUserName() {
    return userName;
  }

  public User setUserName(String userName) {
    return new User(
            this.getUid(),
            this.getEmail(),
            userName,
            this.getFollowing(),
            this.getFollowers(),
            this.getPassword()
    );
  }

  public List<String> getFollowing() {
    return following;
  }

  public User setFollowing(List<String> following) {
    return new User(
            this.getUid(),
            this.getEmail(),
            this.getUserName(),
            following,
            this.getFollowers(),
            this.getPassword()
    );
  }

  public List<String> getFollowers() {
    return followers;
  }

  public User setFollowers(List<String> followers) {
    return new User(
            this.getUid(),
            this.getEmail(),
            this.getUserName(),
            this.getFollowing(),
            followers,
            this.getPassword()
    );
  }
}
