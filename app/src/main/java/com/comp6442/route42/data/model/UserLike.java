package com.comp6442.route42.data.model;

import androidx.annotation.NonNull;

import com.comp6442.route42.data.repository.UserRepository;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

public class UserLike extends Model {
    public String uid;
    public String postId;
    public UserLike(@NonNull String postId, @NonNull String uid) {
        this.uid = uid;
        this.postId = postId;

    }

    /**
     * Returns map object for insertion to collection
     * @return HashMap
     */
    public HashMap<String, String> getObject() {
        DocumentReference userRef = UserRepository.getInstance().getOne(uid);
        HashMap<String, String > res = new HashMap<>(1);
        res.put("uid",userRef.getPath());
        return res;
    }
}
