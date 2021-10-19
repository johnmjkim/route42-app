package com.comp6442.route42.model;

import com.comp6442.route42.data.model.Post;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class PostTest {
  Post post = new Post();
  List hashTagList = new ArrayList();
  List likedBy = new ArrayList();
  Double lat = -33.865;
  Double lon = 151.209;
  Date date;

  private void setRelations() {
    likedBy.addAll(Arrays.asList("Chris", "Robin", "Kyle"));
    post.setLikeCount(10);
    post.setLikedBy(likedBy);
  }

  private void setPostInformation() {
    hashTagList.addAll(Arrays.asList("#morning", "#evening"));
    post.setUserName("abigail47");
    post.setHashtags(hashTagList);
    post.setPostDescription("Station");
    post.setProfilePicUrl("https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");
    post.setImageUrl("https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400");
    post.setIsPublic(0);
  }

  private void setGeoInformation() {
    post.setLatitude(lat);
    post.setLongitude(lon);
    post.setGeohash(String.valueOf("lat/lng: (" + lat + "," + lon + ")"));
    post.setLocationName("Sydney");
  }

  private void setInformation() {
    setPostInformation();
    setGeoInformation();
  }

  @Before
  public void setupTest() {
    setRelations();
    setInformation();
  }

  @Test
  public void checkName() {
    Assert.assertEquals("abigail47", post.getUserName());
  }

  @Test
  public void checkHashTag() {
    Assert.assertEquals(hashTagList, post.getHashtags());
  }

  @Test
  public void checkGeoHash() {
    Assert.assertEquals("lat/lng: (-33.865,151.209)", post.getGeohash());
  }

//  @Test
//  public void checkDateTime() throws ParseException {
//    String expiryDateString = "2018-10-15T17:52:00";
//    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
//    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//    Date date = formatter.parse(expiryDateString);
//    Assert.assertEquals(expiryDateString, formatter.format(date));
//
//    java.util.Date d = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(resultdate);
//    post.setPostDatetime(d);
////    Assert.assertEquals(formatter.format(date),"Wed Jul 17 13:07:19 KST 2019");
//    Assert.assertEquals(post.getPostDatetime(),"Wed Jul 17 13:07:19 KST 2019");
//  }

  @Test
  public void checkLatLng() {
    LatLng latLng = new LatLng(lat, lon);
    Assert.assertEquals(latLng, post.getLatLng());
  }

  @Test
  public void checkDescription() {
    Assert.assertEquals("Station", post.getPostDescription());
  }

  @Test
  public void checkLocation() {
    Assert.assertEquals("Sydney", post.getLocationName());
  }

  @Test
  public void checkLikeCount() {
    Assert.assertEquals(10, post.getLikeCount());
  }

  @Test
  public void checkLikedBy() {
    Assert.assertEquals(likedBy, post.getLikedBy());
  }

  @Test
  public void checkUrl() {
    Assert.assertEquals("https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200", post.getProfilePicUrl());
    Assert.assertEquals("https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400", post.getImageUrl());

  }
  @Test
  public void checkPublic() {
    Assert.assertEquals(0, post.getIsPublic());
  }

//  @Test
//  public void toStringTest() {
//    Post post1 = new Post(null, "hyro", 0,
//            "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200",
//            date, "Station", "Sydney", -33.865, 151.209, hashTagList, 10,
//            "https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400", likedBy);
//    Assert.assertEquals(post1.toString(), "Post{id='5f141152-3f2d-4616-a3d1-33df0b6130de', uid=null, userName='hyro', isPublic=0, profilePicUrl='https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200', postDatetime=null, postDescription='Station', imageUrl='https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400', hashtags=[], likeCount=10, likedBy=[], locationName='Sydney', latitude=-33.865, longitude=151.209, geohash=r3gx2g5414}");
//  }
}
