package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@IgnoreExtraProperties
public class Post {
  private String postId;
  private DocumentReference uid;
  private String userName;
  private int isPublic;
  private List<TsPoint> route = new ArrayList<>();;
  private Activity activity;
  private Timestamp startTs;
  private Timestamp endTs;
  private List<String> hashtags = new ArrayList<>();;
  @ServerTimestamp private Date postDatetime; // will be null on emulator

  // TODO
  // private Double distance;
  // private Double pace;
  // private String imageUrl;

  public Post() {

  }

  public Post(@NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, int isPublic) {
    this(UUID.randomUUID().toString(), uid, userName, route, activity, startTs, endTs, isPublic);
  }

  public Post(@NonNull String postId, @NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, @NonNull int isPublic) {
    this.postId = postId;
    this.uid = uid;
    this.userName = userName;
    this.route = route;
    this.activity = activity;
    this.startTs = startTs;
    this.endTs = endTs;
    this.isPublic = isPublic;
  }

  public Post(String postId, @NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, @NonNull int isPublic, @NonNull List<String> hashtags) {
    this(postId, uid, userName, route, activity, startTs, endTs, isPublic);
    setHashtags(hashtags);
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

  @NonNull
  public int getIsPublic() {
    return isPublic;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }

  public void setUid(DocumentReference uid) {
    this.uid = uid;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setIsPublic(int isPublic) {
    this.isPublic = isPublic;
  }

  public void setRoute(List<TsPoint> route) {
    this.route = route;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void setStartTs(Timestamp startTs) {
    this.startTs = startTs;
  }

  public void setEndTs(Timestamp endTs) {
    this.endTs = endTs;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags;
  }

  public void setPostDatetime(Date postDatetime) {
    this.postDatetime = postDatetime;
  }

  @Override
  public String toString() {
    return "Post{" +
            "postId='" + postId + '\'' +
            ", uid=" + uid +
            ", userName='" + userName + '\'' +
            ", isPublic=" + isPublic +
            ", route=" + route +
            ", activity=" + activity +
            ", startTs=" + startTs +
            ", endTs=" + endTs +
            ", hashtags=" + hashtags +
            ", postDatetime=" + postDatetime +
            '}';
  }
}
