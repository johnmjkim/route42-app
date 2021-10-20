package com.comp6442.route42.model;

import static org.mockito.Mockito.mock;
import com.comp6442.route42.data.model.Post;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
  DocumentReference documentReference;
  Date date = new Date();

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
    post.setPostDatetime(date);
  }

  private void setGeoInformation() {
    post.setLatitude(lat);
    post.setLongitude(lon);
    post.setGeohash(String.valueOf("lat/lng: (" + lat + "," + lon + ")"));
    post.setLocationName("Sydney");
  }

  private void setDocumentReference() {
    documentReference = mock(DocumentReference.class);
    post.setUid(documentReference);
  }

  private void setInformation() {
    setPostInformation();
    setGeoInformation();
    setDocumentReference();
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

  @Test
  public void checkLongitude() {
    Assert.assertEquals(151.209, post.getLongitude(),0.001);
  }

  @Test
  public void checkLatitude() {
    Assert.assertEquals(-33.865, post.getLatitude(),0.001);
  }
  @Test
  public void checkUid() {
    Assert.assertEquals(documentReference, post.getUid());
  }

  @Test
  public void checkDescribeContent() {
    Assert.assertEquals(0, post.describeContents());
  }

  @Test
  public void checkDateTime() {
    Assert.assertEquals(date, post.getPostDatetime());
  }
  //writetoparcel
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
  public void HashtagCheck(){
    String testcase1 = "test1 test2 #test3";
    String testcase2 = "test1 test2 #test3, test4 #test5";
    Assert.assertEquals(Post.getHashTagsFromTextInput(testcase1).toString(),"[#test3]");
    Assert.assertEquals(Post.getHashTagsFromTextInput(testcase2).toString(),"[#test3, #test5]");
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

  @Test
  public void toStringTest() {
    Post post1 = new Post(post.getId(), documentReference, "hyro", 0,
            "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200",
            date, "Station", "Sydney", -33.865, 151.209, hashTagList, 10,
            "https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400", likedBy);
    Assert.assertEquals(post1.toString(), "Post{id='"+post.getId()+"', uid="+documentReference+", userName='hyro', isPublic=0, profilePicUrl='https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200', postDatetime=" + date +", postDescription='Station', locationName='Sydney', latitude=-33.865, longitude=151.209, route=[], geohash='r3gx2g5414', hashtags=[#morning, #evening], likeCount=10, imageUrl='https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400', likedBy=[Chris, Robin, Kyle]}");

    Post post2 = new Post(documentReference, "hiro", 0, "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200",
            date, "Station", "Sydney",  -33.865, 151.209, hashTagList, 10,
            "https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400", likedBy);
    Assert.assertEquals(post2.toString(), "Post{id='"+post2.getId()+"', uid="+documentReference+", userName='hiro', isPublic=0, profilePicUrl='https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200', postDatetime=" + date +", postDescription='Station', locationName='Sydney', latitude=-33.865, longitude=151.209, route=[], geohash='r3gx2g5414', hashtags=[#morning, #evening], likeCount=10, imageUrl='https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400', likedBy=[Chris, Robin, Kyle]}");

  }


}
