package com.comp6442.route42.data.repository;


import com.comp6442.route42.data.model.UserLike;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public  class UserLikeRepository   {
    protected  FirebaseFirestore firestore;
    private static UserLikeRepository instance;

    private UserLikeRepository() {
       firestore =  FirestoreRepository.firestore;
    }
    public static   UserLikeRepository getInstance () {
        if(instance == null) {
            instance = new UserLikeRepository();
        }
        return instance;
    }
    public void getOne(UserLike item) {
        firestore.collection("posts").document(item.postId).collection("userLikes").document(item.uid);

    }
    public void deleteOne(UserLike item) {
        firestore.collection("posts").document(item.postId).collection("userLikes").document(item.uid)
                .delete();
    }
    public Task<DocumentSnapshot> exists(String postId, String uid) {
        return firestore.collection("posts").document(postId).collection("userLikes").document(uid).get();
    }

    public void createOne(UserLike item) {

        firestore.collection("posts").document(item.postId).collection("userLikes").document(item.uid).set(item.getObject());
    }

    public void createMany(List<UserLike> items) {
        // batch size limit is 500 documents
        int idx = 0;
        while (idx < items.size()) {
            int counter = 0;
            // Get a new write batch
            WriteBatch batch = firestore.batch();

            while (counter < 500 && idx < items.size()) {
                UserLike userLike = items.get(idx);
                batch.set(firestore.collection("posts").document(userLike.postId).collection("userLikes").document(userLike.uid), userLike.getObject());
                counter++;
                idx++;
            }
            // Commit the batch
            batch.commit().addOnFailureListener(Timber::e)
                    .addOnSuccessListener(task -> Timber.i("Batch write complete: userLikes"));
        }

    }
    public void setMany(List<UserLike> items) {
        // batch size limit is 500 documents
        int idx = 0;
        while (idx < items.size()) {
            int counter = 0;
            // Get a new write batch
            WriteBatch batch = firestore.batch();

            while (counter < 500 && idx < items.size()) {
                UserLike userLike = items.get(idx);
                batch.set(firestore.collection("posts").document(userLike.postId).collection("userLikes").document(userLike.uid), userLike.getObject()
                , SetOptions.merge());
                counter++;
                idx++;
            }
            // Commit the batch
            batch.commit().addOnFailureListener(Timber::e)
                    .addOnSuccessListener(task -> Timber.i("Batch write complete: userLikes"));
        }

    }

    public List<UserLike> deserializeJSON(InputStream inputStream) {
        JsonObject jsonObject = JsonParser.parseReader(new JsonReader(new InputStreamReader(inputStream))).getAsJsonObject();
        Set<String> postIds = jsonObject.keySet();
        List<UserLike> userLikesList= new ArrayList<>();
        postIds.forEach(postId ->{
//            Timber.i("postId: " + postId);
               jsonObject.get(postId).getAsJsonArray().forEach( uid -> {
                  userLikesList.add(new UserLike( postId, uid.getAsString()) );
              });
        });
        return userLikesList;
    }
}