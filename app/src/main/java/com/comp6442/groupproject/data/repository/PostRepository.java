package com.comp6442.groupproject.data.repository;

import com.comp6442.groupproject.BuildConfig;
import com.comp6442.groupproject.data.model.Post;
import com.comp6442.groupproject.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.List;

import timber.log.Timber;

public final class PostRepository extends FirestoreRepository<Post> {
  private static PostRepository instance = null;

  private PostRepository(FirebaseFirestore firestore) {
    super(firestore, "posts", Post.class);
  }

  public static PostRepository getInstance() {
    if (PostRepository.instance == null) {
      // TODO check if emulator is already running
      FirebaseFirestore firestore = FirebaseFirestore.getInstance();

      if (BuildConfig.DEBUG) {
        try {
          firestore.useEmulator("10.0.2.2", 8080);
        } catch (IllegalStateException exc) {
          Timber.d(exc);
        }
      }

      // disable caching, always pull server data
      FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
              .setPersistenceEnabled(false)
              .build();

      firestore.setFirestoreSettings(settings);
      PostRepository.instance = new PostRepository(firestore);
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
      return UserRepository.getInstance().getUser(str);
    }).create();
  }

  public DocumentReference getPost(String postId) {
    return this.collection.document(postId);
  }

  public Task<QuerySnapshot> getPosts(String uid) {
    return this.collection.whereEqualTo("uid", uid).get();
  }

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

  public void addPost(Post post) {
    // add post only if id does not exist in collection
    this.collection.document(post.getPostId())
            .set(post)
            .addOnSuccessListener(unused -> Timber.i("Insert succeeded: %s", post.toString()))
            .addOnFailureListener(Timber::e);
  }

  public void addPosts(List<Post> posts) {
    // batch size limit is 500 documents
    int idx = 0;
    while (idx < posts.size()) {
      int counter = 0;
      // Get a new write batch
      WriteBatch batch = this.firestore.batch();

      while (counter < 500 && idx < posts.size()) {
        Post post = posts.get(idx);
        DocumentReference postRef = this.collection.document(post.getPostId());
        batch.set(postRef, post);
        counter++;
        idx++;
      }
      // Commit the batch
      batch.commit().addOnFailureListener(Timber::e)
              .addOnSuccessListener(task -> Timber.i("Batch write complete: posts"));
    }
  }
}