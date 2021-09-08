package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@IgnoreExtraProperties
public class User {
  private String uid;

  private String email;

  private String userName;

  @Exclude
  private String password = null;

  private List<DocumentReference> following;
  private List<DocumentReference> followers;

  public User() {

  }

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

  public User(String uid, @NonNull String email, @NonNull String userName, List<DocumentReference> following, List<DocumentReference> followers) {
    this.uid = uid;
    this.email = email;
    this.userName = userName;
    this.following = following;
    this.followers = followers;
  }

  public User(String uid, @NonNull String email, @NonNull String userName, List<DocumentReference> following, List<DocumentReference> followers, String password) {
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

    if (data.containsKey("following"))
      user.setFollowing((List<DocumentReference>) data.get("following"));
    if (data.containsKey("followers"))
      user.setFollowers((List<DocumentReference>) data.get("followers"));
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

  public List<DocumentReference> getFollowing() {
    return following;
  }

  public User setFollowing(List<DocumentReference> following) {
    return new User(
            this.getUid(),
            this.getEmail(),
            this.getUserName(),
            following,
            this.getFollowers(),
            this.getPassword()
    );
  }

  public List<DocumentReference> getFollowers() {
    return followers;
  }

  public User setFollowers(List<DocumentReference> followers) {
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
