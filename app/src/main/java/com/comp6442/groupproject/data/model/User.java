package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@IgnoreExtraProperties
public class User {
  private String uid;
  private String email;
  private String userName;
  private int isPublic;
  private String profilePicUrl;
  private List<DocumentReference> blockedBy = new ArrayList<>();
  private List<DocumentReference> following = new ArrayList<>();
  private List<DocumentReference> followers = new ArrayList<>();
  @Exclude private String password = null;

  public User() {

  }

  public User(@NonNull String email, @NonNull String userName) {
    this(UUID.randomUUID().toString(), email, userName);
  }

  public User(String uid, @NonNull String email, @NonNull String userName) {
    setUid(uid);
    setEmail(email);
    setUserName(userName);
  }

  public User(String uid, @NonNull String email, @NonNull String userName, String password) {
    this(uid, email, userName);
    setPassword(password);
  }

  public User(String uid, @NonNull String email, @NonNull String userName, List<DocumentReference> following, List<DocumentReference> followers, String password, int isPublic, String profilePicUrl, List<DocumentReference> blockedBy) {
    this(uid, email, userName, password);
    setFollowing(following);
    setFollowers(followers);
    setIsPublic(isPublic);
    setProfilePicUrl(profilePicUrl);
    setBlockedBy(blockedBy);
  }

  @NonNull
  @Override
  public String toString() {
    return "User{" +
            "uid='" + uid + '\'' +
            ", email='" + email + '\'' +
            ", userName='" + userName + '\'' +
            ", isPublic=" + isPublic +
            ", profilePicUrl='" + profilePicUrl + '\'' +
            ", blockedBy=" + blockedBy +
            ", following=" + following +
            ", followers=" + followers +
            ", password='" + password + '\'' +
            '}';
  }

  public String getUid() {
    return uid;
  }

  public String getEmail() {
    return email;
  }

  public String getUserName() {
    return userName;
  }

  public int getIsPublic() {
    return isPublic;
  }

  public String getProfilePicUrl() {
    return profilePicUrl;
  }

  public List<DocumentReference> getBlockedBy() {
    return blockedBy;
  }

  public List<DocumentReference> getFollowing() {
    return following;
  }

  public List<DocumentReference> getFollowers() {
    return followers;
  }

  public String getPassword() {
    return password;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setIsPublic(int isPublic) {
    this.isPublic = isPublic;
  }

  public void setProfilePicUrl(String profilePicUrl) {
    this.profilePicUrl = profilePicUrl;
  }

  public void setBlockedBy(List<DocumentReference> blockedBy) {
    this.blockedBy = blockedBy;
  }

  public void setFollowing(List<DocumentReference> following) {
    this.following = following;
  }

  public void setFollowers(List<DocumentReference> followers) {
    this.followers = followers;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public User updateUid(String uid) {
    return new User(
            uid,
            this.getEmail(),
            this.getUserName(),
            this.getFollowing(),
            this.getFollowers(),
            this.getPassword(),
            this.getIsPublic(),
            this.getProfilePicUrl(),
            this.getBlockedBy()
    );
  }

  public User updateEmail(@NonNull String email) {
    return new User(
            this.getUid(),
            email,
            this.getUserName(),
            this.getFollowing(),
            this.getFollowers(),
            this.getPassword(),
            this.getIsPublic(),
            this.getProfilePicUrl(),
            this.getBlockedBy()
    );
  }

  public User updateUserName(String userName) {
    return new User(
            this.getUid(),
            this.getEmail(),
            userName,
            this.getFollowing(),
            this.getFollowers(),
            this.getPassword(),
            this.getIsPublic(),
            this.getProfilePicUrl(),
            this.getBlockedBy()
    );
  }

  public User updateFollowing(List<DocumentReference> following) {
    return new User(
            this.getUid(),
            this.getEmail(),
            this.getUserName(),
            following,
            this.getFollowers(),
            this.getPassword(),
            this.getIsPublic(),
            this.getProfilePicUrl(),
            this.getBlockedBy()
    );
  }

  public User updateFollowers(List<DocumentReference> followers) {
    return new User(
            this.getUid(),
            this.getEmail(),
            this.getUserName(),
            this.getFollowing(),
            followers,
            this.getPassword(),
            this.getIsPublic(),
            this.getProfilePicUrl(),
            this.getBlockedBy()
    );
  }

  public User updatePrivacy(int isPublic) {
    return new User(
            this.getUid(),
            this.getEmail(),
            this.getUserName(),
            this.getFollowing(),
            this.getFollowers(),
            this.getPassword(),
            isPublic,
            this.getProfilePicUrl(),
            this.getBlockedBy()
    );
  }
}
