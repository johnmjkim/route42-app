package com.comp6442.route42.data.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Post extends Model {
  private DocumentReference uid;
  private String userName;
  private int isPublic;
  private String profilePicUrl;
  @ServerTimestamp private Date postDatetime;
  private String postDescription = "";
  private String locationName;
  private Double latitude;
  private Double longitude;
  private List<String> hashtags = new ArrayList<>();

  private int likeCount = 0;
  private String imageUrl;
  private List<DocumentReference> likedBy = new ArrayList<>();

  public Post() {
  }

  public Post(String id, DocumentReference uid, String userName, int isPublic, String profilePicUrl, Date postDatetime, String postDescription, String locationName, Double latitude, Double longitude, List<String> hashtags, int likeCount, String imageUrl, List<DocumentReference> likedBy) {
    this.id = id;
    this.uid = uid;
    this.userName = userName;
    this.isPublic = isPublic;
    this.profilePicUrl = profilePicUrl;
    this.postDatetime = postDatetime;
    this.postDescription = postDescription;
    this.locationName = locationName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.hashtags = hashtags;
    this.likeCount = likeCount;
    this.imageUrl = imageUrl;
    this.likedBy = likedBy;
  }

  public Post(DocumentReference uid, String userName, int isPublic, String profilePicUrl, Date postDatetime, String postDescription, String locationName, Double latitude, Double longitude, List<String> hashtags, int likeCount, String imageUrl, List<DocumentReference> likedBy) {
    super();
    this.uid = uid;
    this.userName = userName;
    this.isPublic = isPublic;
    this.profilePicUrl = profilePicUrl;
    this.postDatetime = postDatetime;
    this.postDescription = postDescription;
    this.locationName = locationName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.hashtags = hashtags;
    this.likeCount = likeCount;
    this.imageUrl = imageUrl;
    this.likedBy = likedBy;
  }

  public DocumentReference getUid() {
    return uid;
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

  public Date getPostDatetime() {
    return postDatetime;
  }

  public String getPostDescription() {
    return postDescription;
  }

  public String getLocationName() {
    return locationName;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public List<String> getHashtags() {
    return hashtags;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public List<DocumentReference> getLikedBy() {
    return likedBy;
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

  public void setProfilePicUrl(String profilePicUrl) {
    this.profilePicUrl = profilePicUrl;
  }

  public void setPostDatetime(Date postDatetime) {
    this.postDatetime = postDatetime;
  }

  public void setPostDescription(String postDescription) {
    this.postDescription = postDescription;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setLikedBy(List<DocumentReference> likedBy) {
    this.likedBy = likedBy;
  }

  @Override
  public String toString() {
    return "Post{" +
            "id='" + id + '\'' +
            ", uid=" + uid +
            ", userName='" + userName + '\'' +
            ", isPublic=" + isPublic +
            ", profilePicUrl='" + profilePicUrl + '\'' +
            ", postDatetime=" + postDatetime +
            ", postDescription='" + postDescription + '\'' +
            ", locationName='" + locationName + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", hashtags=" + hashtags +
            ", likeCount=" + likeCount +
            ", imageUrl='" + imageUrl + '\'' +
            ", likedBy=" + likedBy +
            '}';
  }
}
