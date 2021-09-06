package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {
  @NonNull
  private final String uid;
  @NonNull
  private final String postId;
  @NonNull
  private final String userName;

  public Post(@NonNull String uid, @NonNull String postId, @NonNull String userName) {
    this.uid = uid;
    this.postId = postId;
    this.userName = userName;
  }

  @NonNull
  public String getUid() {
    return uid;
  }

  @NonNull
  public String getPostId() {
    return postId;
  }

  @NonNull
  public String getUserName() {
    return userName;
  }

  @Override
  public String toString() {
    return "Post{" +
            "uid='" + uid + '\'' +
            ", postId='" + postId + '\'' +
            ", userName='" + userName + '\'' +
            '}';
  }
}
