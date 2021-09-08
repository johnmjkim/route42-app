package com.comp6442.groupproject.data.repository;

import com.comp6442.groupproject.BuildConfig;
import com.comp6442.groupproject.data.model.Post;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.WriteBatch;

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
          Timber.w(exc);
        }
      }

      FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
              .setPersistenceEnabled(false)
              .build();

      firestore.setFirestoreSettings(settings);
      PostRepository.instance = new PostRepository(firestore);
    }
    return PostRepository.instance;
  }

  public DocumentReference getPost(String postId) {
    return this.collection.document(postId);
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
        // post.getRoute().forEach(point -> {
        //   DocumentReference pointDoc = postRef.collection("route").document();
        //   batch.set(pointDoc, point);
        // });
        counter++;
        idx++;
      }
      // Commit the batch
      batch.commit().addOnFailureListener(Timber::e)
              .addOnSuccessListener(task -> Timber.i("Batch write complete: posts"));
    }
  }
}