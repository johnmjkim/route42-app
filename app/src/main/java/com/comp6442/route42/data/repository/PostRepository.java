package com.comp6442.route42.data.repository;

import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.List;

import timber.log.Timber;

public class PostRepository extends FirestoreRepository<Post> {
  private static PostRepository instance = null;

  private PostRepository() {
    super("posts", Post.class);
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
    }).registerTypeAdapter(Double.class, (JsonDeserializer<Double>) (json, type, context) -> {
      return json.getAsDouble();
    }).registerTypeAdapter(DocumentReference.class, (JsonDeserializer<DocumentReference>) (json, type, context) -> {
      String str = json.toString();
      return UserRepository.getInstance().getOne(str);
    }).create();
  }

  public DocumentReference getOne(String postId) {
    return this.collection.document(postId);
  }

  public Task<QuerySnapshot> getMany(String uid) {
    return this.collection.whereEqualTo("uid", uid).get();
  }

  /**
   * Get posts by users that did not block the current user, and are public.
   * @param user
   * @param limit
   * @return
   */
  public Query getVisiblePosts(User user, int limit) {
    if (user.getBlockedBy().size() > 0) {
      Timber.i("breadcrumb");
      return this.collection.whereNotIn("uid", user.getBlockedBy())
              .whereEqualTo("isPublic", 1).limit(limit);
    } else {
      Timber.i("breadcrumb");
      return this.collection.whereEqualTo("isPublic", 1).limit(limit);
    }
  }

  public void createOne(Post post) {
    // add post only if id does not exist in collection
    this.collection.document(post.getId())
            .set(post)
            .addOnSuccessListener(unused -> Timber.i("Insert succeeded: %s", post.toString()))
            .addOnFailureListener(Timber::e);
  }
  public void like(Post post) {
    this.collection.document(post.getId())
    .update("likeCount", post.getLikeCount()+1);
  }
  public void unlike(Post post) {
    this.collection.document(post.getId())
            .update("likeCount", post.getLikeCount()-1);
  }
  public void createMany(List<Post> posts) {
    // batch size limit is 500 documents
    int idx = 0;
    while (idx < posts.size()) {
      int counter = 0;
      // Get a new write batch
      WriteBatch batch = firestore.batch();

      while (counter < 500 && idx < posts.size()) {
        Post post = posts.get(idx);
        DocumentReference postRef = this.collection.document(post.getId());
        batch.set(postRef, post);
        counter++;
        idx++;
      }
      // Commit the batch
      batch.commit().addOnFailureListener(Timber::e)
              .addOnSuccessListener(task -> Timber.i("Batch write complete: posts"));
    }
  }

  public void setMany(List<Post> posts) {
    int idx = 0;
    while (idx < posts.size()) {
      int counter = 0;
      WriteBatch batch = firestore.batch();

      while (counter < 500 && idx < posts.size()) {
        Post post = posts.get(idx);
        DocumentReference postRef = this.collection.document(post.getId());
        batch.set(postRef, post, SetOptions.merge());

        counter++;
        idx++;
      }
      // Commit the batch
      batch.commit().addOnFailureListener(Timber::e)
              .addOnSuccessListener(task -> Timber.i("Batch set complete: posts"));
    }
  }
}