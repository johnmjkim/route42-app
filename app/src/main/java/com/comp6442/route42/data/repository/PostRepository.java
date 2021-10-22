package com.comp6442.route42.data.repository;

import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class PostRepository extends FirestoreRepository<Post> {
  private static PostRepository instance = null;

  private PostRepository() {
    super("posts", Post.class);
  }

  public Query getMany(String uid, int limit) {
    DocumentReference docRef = UserRepository.getInstance().getOne(uid);
    return super.collection.whereEqualTo("uid", docRef)
            .orderBy("postDatetime", Query.Direction.DESCENDING)
            .limit(limit);
  }

  /**
   * Get posts by users that did not block the current user, and are public.
   * TODO: limitation - only supports up to 10 blocked
   */
  public Query getVisiblePosts(User user, int limit) {
    Timber.d("breadcrumb");
    if (user.getBlockedBy().size() > 0 && user.getBlocked().size() > 0) {
      return this.collection
              .whereEqualTo("isPublic", 1)
              .whereNotIn("uid", user.getBlockedBy().subList(0, Math.min(10, user.getBlockedBy().size())))
              .whereNotIn("uid", user.getBlocked().subList(0, Math.min(10, user.getBlocked().size())))
              .orderBy("uid", Query.Direction.ASCENDING)
              .orderBy("postDatetime", Query.Direction.DESCENDING)
              .limit(limit);
    } else if (user.getBlocked().size() > 0) {
      return this.collection
              .whereEqualTo("isPublic", 1)
              .whereNotIn("uid", user.getBlocked().subList(0, Math.min(10, user.getBlocked().size())))
              .orderBy("uid", Query.Direction.ASCENDING)
              .orderBy("postDatetime", Query.Direction.DESCENDING)
              .limit(limit);
    } else if (user.getBlockedBy().size() > 0) {
      return this.collection
              .whereEqualTo("isPublic", 1)
              .whereNotIn("uid", user.getBlockedBy().subList(0, Math.min(10, user.getBlockedBy().size())))
              .orderBy("uid", Query.Direction.ASCENDING)
              .orderBy("postDatetime", Query.Direction.DESCENDING)
              .limit(limit);
    } else {
      return this.collection
              .whereEqualTo("isPublic", 1)
              .orderBy("postDatetime", Query.Direction.DESCENDING)
              .limit(limit);
    }
  }

  /**
   * The character \uf8ff used in the query is a very high code point in the Unicode range
   * (it is a Private Usage Area [PUA] code).
   * Because it is after most regular characters in Unicode, the query matches all values that start with queryText.
   */
  public Query searchByNamePrefix(User user, String name, int limit) {
    // TODO: temporarily removed filter on isBlockedBy since unblock button is inside each profile
    Timber.i("Searching for posts matching %s", name);
    if (user.getBlockedBy().size() == 0) {
      Timber.d("breadcrumb");
      return this.collection
              .whereGreaterThanOrEqualTo("userName", name)
              .whereLessThanOrEqualTo("userName", name + "\uF7FF")
              .limit(limit);
    } else {
      Timber.d("breadcrumb");
      return this.collection
              .whereGreaterThanOrEqualTo("userName", name)
              .whereLessThanOrEqualTo("userName", name + "\uF7FF")
              .limit(limit);
    }
  }

  public List<Task<QuerySnapshot>> getPostsWithinRadius(LatLng location, double radiusInM, int limit) {
    // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
    // a separate query for each pair. There can be up to 9 pairs of bounds
    // depending on overlap, but in most cases there are 4.
    List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(
            new GeoLocation(location.latitude, location.longitude),
            radiusInM
    );
    final List<Task<QuerySnapshot>> tasks = new ArrayList<>();

    // todo - only select followed / followers?
    for (GeoQueryBounds b : bounds) {
      Query query = this.collection
              .orderBy("geohash")
              .startAt(b.startHash)
              .endAt(b.endHash)
              .whereEqualTo("isPublic", 1)
              .limit(limit);

      tasks.add(query.get());
    }
    return tasks;
  }

  public void like(Post post, String uid) {
    WriteBatch batch = firestore.batch();
    DocumentReference docRef = this.collection.document(post.getId());
    DocumentReference userRef = UserRepository.getInstance().getOne(uid);

    docRef.update("likeCount", FieldValue.increment(1));
    docRef.update("likedBy", FieldValue.arrayUnion(userRef));
    batch.commit()
            .addOnFailureListener(Timber::e)
            .addOnSuccessListener(task -> Timber.i("Like event recorded: %s -> %s", uid, post));
  }
  public void scheduleLike(String postId, String uid) {
    WriteBatch batch = firestore.batch();
    DocumentReference docRef = this.collection.document(postId);
    DocumentReference userRef = UserRepository.getInstance().getOne(uid);
    docRef.update("likeCount", FieldValue.increment(1));
    docRef.update("likedBy", FieldValue.arrayUnion(userRef));
    batch.commit()
            .addOnFailureListener(Timber::e)
            .addOnSuccessListener(task -> Timber.i("Scheduled Like event recorded: %s -> %s", uid, postId));
  }
  public void unlike(Post post, String uid) {
    WriteBatch batch = firestore.batch();
    DocumentReference docRef = this.collection.document(post.getId());
    DocumentReference userRef = UserRepository.getInstance().getOne(uid);

    docRef.update("likeCount", FieldValue.increment(-1));
    docRef.update("likedBy", FieldValue.arrayRemove(userRef));
    batch.commit()
            .addOnFailureListener(Timber::e)
            .addOnSuccessListener(task -> Timber.i("Unlike event recorded: %s -> %s", uid, post));
  }

  public static PostRepository getInstance() {
    if (PostRepository.instance == null) {
      PostRepository.instance = new PostRepository();
    }
    return PostRepository.instance;
  }

  public static Gson getJsonDeserializer() {
    return new GsonBuilder().registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, type, context) -> {
      String tsString = json.toString();
      int decimalIdx = (tsString.contains(".")) ? tsString.indexOf(".") : tsString.length();
      return new Timestamp(
              Long.parseLong(tsString.substring(0, decimalIdx)),
              (decimalIdx != tsString.length()) ? Integer.parseInt(tsString.substring(decimalIdx + 1)) : 0
      );
    }).registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, type, context) -> {
      String tsString = json.toString();
      return new Date(Long.parseLong(tsString));
    }).registerTypeAdapter(Double.class, (JsonDeserializer<Double>) (json, type, context) -> {
      return json.getAsDouble();
    }).registerTypeAdapter(DocumentReference.class, (JsonDeserializer<DocumentReference>) (json, type, context) -> {
      String str = json.toString();
      if (str.contains("\"")) str = str.replaceAll("^\"|\"$", "");
      return UserRepository.getInstance().getOne(str);
    }).create();
  }
}