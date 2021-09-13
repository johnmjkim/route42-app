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
  private Date postDatetime;
  private String profilePicUrl;

  @Override
  public String toString() {
    return "Post{" +
            "uid=" + uid +
            ", userName='" + userName + '\'' +
            ", isPublic=" + isPublic +
            ", route=" + route +
            ", activity=" + activity +
            ", startTs=" + startTs +
            ", endTs=" + endTs +
            ", hashtags=" + hashtags +
            ", postDatetime=" + postDatetime +
            ", profilePicUrl='" + profilePicUrl + '\'' +
            ", likeCount=" + likeCount +
            '}';
  }


  private int likeCount;

  // private String postDescription;
  // private String imageUrl;
  // private Double distance;
  // private Double pace;

  public Post() {

  }

  /* Assign a random UUID */
  public Post(@NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs, int isPublic, List<String> hashtags, String profilePicUrl) {
    this(UUID.randomUUID().toString(), uid, userName, route, activity, startTs, endTs, isPublic, hashtags, profilePicUrl);
  }

  public Post(@NonNull String id, @NonNull DocumentReference uid, @NonNull String userName, @NonNull List<TsPoint> route, @NonNull Activity activity, @NonNull Timestamp startTs, @NonNull Timestamp endTs,  int isPublic, List<String> hashtags, String profilePicUrl) {
    this.id = id;
    this.uid = uid;
    this.userName = userName;
    this.route = route;
    this.activity = activity;
    this.startTs = startTs;
    this.endTs = endTs;
    this.isPublic = isPublic;
    this.hashtags = hashtags;
    this.profilePicUrl = profilePicUrl;
  }

  public DocumentReference getUid() {
    return uid;
  }

  public void setUid(DocumentReference uid) {
    this.uid = uid;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(int isPublic) {
    this.isPublic = isPublic;
  }

  public List<TsPoint> getRoute() {
    return route;
  }

  public void setRoute(List<TsPoint> route) {
    this.route = route;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public Timestamp getStartTs() {
    return startTs;
  }

  public void setStartTs(Timestamp startTs) {
    this.startTs = startTs;
  }

  public Timestamp getEndTs() {
    return endTs;
  }

  public void setEndTs(Timestamp endTs) {
    this.endTs = endTs;
  }

  public List<String> getHashtags() {
    return hashtags;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags;
  }

  public Date getPostDatetime() {
    return postDatetime;
  }

  public void setPostDatetime(Date postDatetime) {
    this.postDatetime = postDatetime;
  }

  public String getProfilePicUrl() {
    return profilePicUrl;
  }

  public void setProfilePicUrl(String profilePicUrl) {
    this.profilePicUrl = profilePicUrl;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public enum Activity {
    Walk, Run, Cycle
  }
}
