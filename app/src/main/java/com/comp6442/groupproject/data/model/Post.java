package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@IgnoreExtraProperties
public class Post {
  private String postId;

  private DocumentReference uid;

  private String userName;

  private List<TsPoint> route;

  private Activity activity;

  private Timestamp startTs;

  private Timestamp endTs;

  private List<String> hashtags;

  // will be null on emulator
  @ServerTimestamp
  private Date postDatetime;

  // TODO
  // private Double distance;
  // private Double pace;
  // private String imageUrl;


  public Post(String postId, @NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, @NonNull List<String> hashtags) {
    this.postId = postId;
    this.uid = uid;
    this.userName = userName;
    this.route = route;
    this.activity = activity;
    this.startTs = startTs;
    this.endTs = endTs;
    this.hashtags = hashtags;
  }

  public Post(@NonNull String postId, @NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs) {
    this.postId = postId;
    this.uid = uid;
    this.userName = userName;
    this.route = route;
    this.activity = activity;
    this.startTs = startTs;
    this.endTs = endTs;
  }

  public Post(@NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs) {
    this(UUID.randomUUID().toString(), uid, userName, route, activity, startTs, endTs);
  }

  public Post() {

  }

  @NonNull
  public DocumentReference getUid() {
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

  @NonNull
  public List<TsPoint> getRoute() {
    return route;
  }

  @NonNull
  public Timestamp getStartTs() {
    return startTs;
  }

  @NonNull
  public Timestamp getEndTs() {
    return endTs;
  }

  @NonNull
  public Activity getActivity() {
    return activity;
  }

  public List<String> getHashtags() {
    return hashtags;
  }

  public Date getPostDatetime() {
    return postDatetime;
  }
}
