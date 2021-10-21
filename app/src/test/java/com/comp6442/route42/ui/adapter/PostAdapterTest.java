package com.comp6442.route42.ui.adapter;

import static org.mockito.Mockito.mock;

import android.view.View;

import com.comp6442.route42.data.model.Post;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PostAdapterTest {
  private final String loggedInUID = "d259b635-1d89-482f-82e5-686582d38cea";
  Post post = new Post();
  List hashTagList = new ArrayList();
  List likedBy = new ArrayList();
  Double lat = -33.865;
  Double lon = 151.209;
  DocumentReference documentReference;
  Date date = new Date();
  private final List<Post> posts = Arrays.asList(
          new Post(post.getId(), documentReference, "hyro", 0,
                  "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200",
                  date, "Station", "Sydney", -33.865, 151.209, hashTagList, 10,
                  "https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400", likedBy));
  PostAdapter postAdapter = new PostAdapter(posts, loggedInUID);

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
    post.setGeohash("lat/lng: (" + lat + "," + lon + ")");
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
  public void checkItemCount() {
    Assert.assertEquals(postAdapter.getItemCount(), 1);
  }

  @Test
  public void checkPost() {
    postAdapter.setPosts(posts);
    Assert.assertEquals(
            "[Post{id='" + post.getId() + "', uid=null, userName='hyro', isPublic=0, profilePicUrl='https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200', postDatetime=" + date + ", postDescription='Station', locationName='Sydney', latitude=-33.865, longitude=151.209, route=[], geohash='r3gx2g5414', hashtags=[#morning, #evening], likeCount=10, imageUrl='https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400', likedBy=[Chris, Robin, Kyle]}]", postAdapter.getPosts().toString());
  }

  @Test
  public void checkUid() {
    Assert.assertEquals(postAdapter.getLoggedInUID(), "d259b635-1d89-482f-82e5-686582d38cea");
  }

  @Test
  public void checkViewHolder() {
    View view = mock(View.class);
    PostAdapter.PostViewHolder postViewHolder = new PostAdapter.PostViewHolder(view);
    Assert.assertNotNull(postViewHolder);
  }
}
