package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@IgnoreExtraProperties
public class Post {
  @NonNull
  private final String postId;

  @NonNull
  private final String uid;

  @NonNull
  private final String userName;

  @NonNull
  private final List<TsPoint> route;

  @NonNull
  private final Activity activity;

  @NonNull
  private final Timestamp startTs;

  @NonNull
  private final Timestamp endTs;

  // will be null on emulator
  @ServerTimestamp
  private Date postDatetime;

  // TODO
  // private final List<String> tags;
  // private Double distance;
  // private Double pace;
  // private String imageUrl;

  public Post(@NonNull String postId, @NonNull String uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs) {
    this.postId = postId;
    this.uid = uid;
    this.userName = userName;
    this.route = route;
    this.activity = activity;
    this.startTs = startTs;
    this.endTs = endTs;
  }

  public Post(@NonNull String uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs) {
    this(UUID.randomUUID().toString(), uid, userName, route, activity, startTs, endTs);
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
  public Post clearRoute() {
    return new Post(getPostId(), getUid(), getUserName(), null, getActivity(), startTs, endTs);
  }

  @NonNull
  public Activity getActivity() {
    return activity;
  }
}
