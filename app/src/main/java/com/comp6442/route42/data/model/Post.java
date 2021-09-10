package com.comp6442.route42.data.model;

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
public class Post extends Model {
  private DocumentReference uid;
  private String userName;
  private int isPublic;
  private List<TsPoint> route = new ArrayList<>();
  private Activity activity;
  private Timestamp startTs;
  private Timestamp endTs;
  private List<String> hashtags = new ArrayList<>();
  @ServerTimestamp
  private Date postDatetime; // will be null on emulator

  // TODO
  // private Double distance;
  // private Double pace;
  // private String imageUrl;

  public Post() {

  }

  public Post(@NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, int isPublic) {
    this(UUID.randomUUID().toString(), uid, userName, route, activity, startTs, endTs, isPublic);
  }

  public Post(@NonNull String id, @NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, @NonNull int isPublic) {
    this.id = id;
    this.uid = uid;
    this.userName = userName;
    this.route = route;
    this.activity = activity;
    this.startTs = startTs;
    this.endTs = endTs;
    this.isPublic = isPublic;
  }

  public Post(String id, @NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, @NonNull int isPublic, @NonNull List<String> hashtags) {
    this(id, uid, userName, route, activity, startTs, endTs, isPublic);
    setHashtags(hashtags);
  }

  @NonNull
  public DocumentReference getUid() {
    return uid;
  }

  public void setUid(DocumentReference uid) {
    this.uid = uid;
  }

  @NonNull
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @NonNull
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @NonNull
  public List<TsPoint> getRoute() {
    return route;
  }

  public void setRoute(List<TsPoint> route) {
    this.route = route;
  }

  @NonNull
  public Timestamp getStartTs() {
    return startTs;
  }

  public void setStartTs(Timestamp startTs) {
    this.startTs = startTs;
  }

  @NonNull
  public Timestamp getEndTs() {
    return endTs;
  }

  public void setEndTs(Timestamp endTs) {
    this.endTs = endTs;
  }

  @NonNull
  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public List<String> getHashtags() {
    return hashtags;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags;
  }

  @NonNull
  public int getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(int isPublic) {
    this.isPublic = isPublic;
  }

  public void setPostDatetime(Date postDatetime) {
    this.postDatetime = postDatetime;
  }

  @Override
  public String toString() {
    return "Post{" +
            "postId='" + id + '\'' +
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

  public enum Activity {
    Walk, Run, Cycle
  }
}
