package com.comp6442.route42.data.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@IgnoreExtraProperties
public class Post extends Model implements Parcelable {
  public static final Creator<Post> CREATOR = new Creator<Post>() {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public Post createFromParcel(Parcel in) {
      return new Post(in);
    }

    @Override
    public Post[] newArray(int size) {
      return new Post[size];
    }
  };

  private DocumentReference uid;
  private String userName;
  private int isPublic;
  private String profilePicUrl;
  @ServerTimestamp
  private Date postDatetime;
  private String postDescription = "";
  private String locationName;
  private Double latitude;
  private Double longitude;
  private List<Point> route = new ArrayList<>();;
  private String geohash = "";
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
    setGeohash();
  }

  public Post(DocumentReference uid, String userName, int isPublic, String profilePicUrl, Date postDatetime,
              String postDescription, String locationName, Double latitude, Double longitude, List<String> hashtags,
              int likeCount, String imageUrl, List<DocumentReference> likedBy) {
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
    setGeohash();
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  protected Post(Parcel in) {
    this.id = in.readString();
    this.uid = FirebaseFirestore.getInstance().document(in.readString());
    this.userName = in.readString();
    this.postDescription = in.readString();
    this.profilePicUrl = in.readString();
    this.imageUrl = in.readString();
    this.isPublic = in.readInt();
    this.likeCount = in.readInt();
    this.locationName = in.readString();
    this.locationName = in.readString();
    this.latitude = in.readDouble();
    this.longitude = in.readDouble();
    this.route = in.readParcelableList(new ArrayList<>(), Point.class.getClassLoader());
    this.geohash = in.readString();
    this.hashtags = in.createStringArrayList();
    this.postDatetime = new Date((Long) in.readValue(Long.class.getClassLoader()));
    in.createStringArrayList().forEach(s -> this.likedBy.add(FirebaseFirestore.getInstance().document(s)));
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(this.id);
    parcel.writeString(this.uid.getPath());
    parcel.writeString(this.userName);
    parcel.writeString(this.postDescription);
    parcel.writeString(this.profilePicUrl);
    parcel.writeString(this.imageUrl);
    parcel.writeInt(this.isPublic);
    parcel.writeInt(this.likeCount);
    parcel.writeString(this.locationName);
    parcel.writeDouble(this.latitude);
    parcel.writeDouble(this.longitude);
    parcel.writeParcelableList(this.route, PARCELABLE_WRITE_RETURN_VALUE);
    parcel.writeString(this.geohash);
    parcel.writeStringList(this.hashtags);

    if (this.postDatetime != null) {
      parcel.writeLong(postDatetime.getTime());
    } else {
      parcel.writeValue(null);
    }

    List<String> likedBySerialized = new ArrayList<>();
    for (DocumentReference docRef : this.likedBy) likedBySerialized.add(docRef.getPath());
    parcel.writeStringList(likedBySerialized);
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

  public String getProfilePicUrl() {
    return profilePicUrl;
  }

  public void setProfilePicUrl(String profilePicUrl) {
    this.profilePicUrl = profilePicUrl;
  }

  public Date getPostDatetime() {
    return postDatetime;
  }

  public void setPostDatetime(Date postDatetime) {
    this.postDatetime = postDatetime;
  }

  public String getPostDescription() {
    return postDescription;
  }

  public void setPostDescription(String postDescription) {
    this.postDescription = postDescription;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
    if (this.latitude != null && this.longitude != null) setGeohash();
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
    if (this.latitude != null && this.longitude != null) setGeohash();
  }

  public List<String> getHashtags() {
    return hashtags;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public List<DocumentReference> getLikedBy() {
    return likedBy;
  }

  public void setLikedBy(List<DocumentReference> likedBy) {
    this.likedBy = likedBy;
  }


  public List<Point> getRoute() {
    return route;
  }

  public void setRoute(List<Point> route) {
    this.route = route;
  }

  public String getGeohash() {
    return geohash;
  }

  public void setGeohash(String geohash) {
    this.geohash = geohash;
  }

  public LatLng getLatLng() {
    return new LatLng(this.latitude, this.longitude);
  }

  public void setGeohash() {
    this.geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
  }

  public static List<String> getHashTagsFromTextInput(String textInput) {
    // TODO fix(done), #test returns #t in previous code
    List<String> hashTags = new ArrayList<>();
    String currentTag = "";
    textInput = textInput.toLowerCase().trim();
    for (int i=0; i<textInput.length(); i++) {
      char c = textInput.charAt(i);
      if(c == '#') {
          for(int j=i;j<textInput.length();j++){
            if(textInput.charAt(j)==' '){
              i=j;
              break;
            }
            if(textInput.charAt(j)==','){
              i=j;
              break;
            }
            currentTag+= textInput.charAt(j);
        }
//        if(c == '#') {
//          if ( currentTag.length()>0) {
//            hashTags.add(currentTag.trim());
//            currentTag = "";
//          }
//          currentTag+= c;

      } else if (currentTag.length()>0 && Pattern.matches("[:space:]" , Character.toString(c))) {
        hashTags.add(currentTag.trim());
        currentTag = "";
      }
      else if (currentTag.length()>0 && Pattern.matches("\\p{Punct}" , Character.toString(c)) ) {
        hashTags.add(currentTag.trim());
        currentTag = "";
      }
//      else if (currentTag.length()>0) {
//        currentTag += c;
//      }
    }
    if (currentTag.length()>0) {
      hashTags.add(currentTag.trim());
    }
    return hashTags;
  }


  @NonNull
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
            ", route=" + route +
            ", geohash='" + geohash + '\'' +
            ", hashtags=" + hashtags +
            ", likeCount=" + likeCount +
            ", imageUrl='" + imageUrl + '\'' +
            ", likedBy=" + likedBy +
            '}';
  }
}
