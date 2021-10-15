package com.comp6442.route42.model;

import com.comp6442.route42.data.model.Post;

import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PostTest {
  Post post = new Post();
  List hashTagList = new ArrayList();
  List likedBy = new ArrayList();
  Date date;

    @Test
    public void checkName() {
      post.setUserName("abigail47");
      Assert.assertEquals(post.getUserName(),"abigail47");
    }

  @Test
  public void checkHashTag() {
    hashTagList.add("#morning");
    hashTagList.add("#evening");
    post.setHashtags(hashTagList);
    Assert.assertEquals(post.getHashtags(),hashTagList);
  }

  @Test
  public void checkGeoHash() {
    post.setGeohash("lat/lng: (-33.865,151.209)");
    Assert.assertEquals(post.getGeohash(),"lat/lng: (-33.865,151.209)");

  }

  @Test
  public void checkDateTime() throws ParseException {
    DateFormat format = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
    Date date = format.parse("2020.10.12 at 11:33:10");
    post.setPostDatetime(date);
    Assert.assertEquals(post.getPostDatetime().toString(),"Mon Oct 12 11:33:10 KST 2020");
  }

  @Test
  public void checkLatLong() {
    post.setLatitude(-33.865);
    post.setLongitude(151.209);
    Assert.assertEquals(post.getLatitude().toString(),"-33.865");
    Assert.assertEquals(post.getLongitude().toString(),"151.209");
    Assert.assertEquals(post.getLatLng().toString(),"lat/lng: (-33.865,151.209)");
    Assert.assertEquals(post.getGeohash(),"r3gx2g5414");

  }

  @Test
  public void checkDescription() {
    post.setPostDescription("Station");
    Assert.assertEquals(post.getPostDescription(),"Station");
  }

  @Test
  public void checkLocation() {
    post.setLocationName("Sydney");
    Assert.assertEquals(post.getLocationName(),"Sydney");
  }

  @Test
  public void checkLikeCount() {
    post.setLikeCount(10);
    Assert.assertEquals(post.getLikeCount(),10);
  }

  @Test
  public void checkLikedBy() {
    likedBy.add("Chris");
    likedBy.add("Robin");
    likedBy.add("Kyle");

    post.setLikedBy(likedBy);
    Assert.assertEquals(post.getLikedBy(),likedBy);
  }



  @Test
  public void checkUrl() {
    post.setProfilePicUrl("https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");
    post.setImageUrl("https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400");
    Assert.assertEquals(post.getProfilePicUrl(),"https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");
    Assert.assertEquals(post.getImageUrl(),"https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400");

  }
  @Test
  public void checkPublic() {
    post.setIsPublic(0);
    Assert.assertEquals(post.getIsPublic(),0);
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
