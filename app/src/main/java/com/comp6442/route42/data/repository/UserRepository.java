package com.comp6442.route42.data.repository;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public final class UserRepository extends FirestoreRepository<User> {
  private static UserRepository instance = null;

  private UserRepository() {
    super("users", User.class);
  }

  public void createOne(FirebaseUser firebaseUser) {
    // add user only if uid does not exist in user collection
    super.createOne(new User(
            Objects.requireNonNull(firebaseUser.getUid()),
            Objects.requireNonNull(firebaseUser.getEmail())
    ));
  }

  /**
   * followActor follows followReceiver
   *
   * @param followActorId
   * @param followReceiverId
   */
  public void follow(String followActorId, String followReceiverId) {
    DocumentReference followActor = this.collection.document(followActorId);
    DocumentReference followReceiver = this.collection.document(followReceiverId);
    followActor.update("following", FieldValue.arrayUnion(followReceiver));
    followReceiver.update("followers", FieldValue.arrayUnion(followActor));
  }

  /**
   * unfollowActor unfollows unfollowReceiver
   *
   * @param unfollowActorId
   * @param unfollowReceiverId
   */
  public void unfollow(String unfollowActorId, String unfollowReceiverId) {
    DocumentReference unfollowActor = this.collection.document(unfollowActorId);
    DocumentReference unfollowReceiver = this.collection.document(unfollowReceiverId);
    unfollowActor.update("following", FieldValue.arrayRemove(unfollowReceiver));
    unfollowReceiver.update("followers", FieldValue.arrayRemove(unfollowActor));
  }

  public void block(String blockerUid, String beingBlockedUid) {
    DocumentReference blocker = this.collection.document(blockerUid);
    DocumentReference userBeingBlocked = this.collection.document(beingBlockedUid);
    this.collection.document(blockerUid).update("blocked", FieldValue.arrayUnion(userBeingBlocked));
    this.collection.document(beingBlockedUid).update("blockedBy", FieldValue.arrayUnion(blocker));

    // automatically unfollow
    unfollow(blockerUid, beingBlockedUid);
  }

  public void unblock(String unblockerUid, String beingUnblockedUid) {
    DocumentReference blocker = this.collection.document(unblockerUid);
    DocumentReference userBeingUnblocked = this.collection.document(beingUnblockedUid);
    this.collection.document(unblockerUid).update("blocked", FieldValue.arrayRemove(userBeingUnblocked));
    this.collection.document(beingUnblockedUid).update("blockedBy", FieldValue.arrayRemove(blocker));
  }

  public void count() {
    collection.get().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        Timber.i("Total number of users: %d", Objects.requireNonNull(task.getResult()).size());
      } else {
        Timber.d("Could not get user count");
      }
    });
  }

  public static UserRepository getInstance() {
    if (UserRepository.instance == null) {
      UserRepository.instance = new UserRepository();
    }
    return UserRepository.instance;
  }

  public static Gson getJsonDeserializer() {
    return new GsonBuilder().registerTypeAdapter(DocumentReference.class, (JsonDeserializer<DocumentReference>) (json, type, context) -> {
      String str = json.toString();
      if (str.contains("\"")) str = str.replaceAll("^\"|\"$", "");
      return UserRepository.getInstance().getOne(str);
    }).create();
  }
}