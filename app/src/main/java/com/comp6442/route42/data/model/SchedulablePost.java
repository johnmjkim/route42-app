package com.comp6442.route42.data.model;

import static com.comp6442.route42.data.model.Post.getHashTagsFromTextInput;

import android.content.Context;

import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.utils.XMLCreator;
import com.google.firebase.firestore.DocumentReference;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;

public class SchedulablePost implements  Schedulable<Post>{
    private final String baseFilename = "scheduled_post";
    private final String storageFilename = baseFilename + new Date().toString() + ".xml";
    private  String snapshotFilePath ;
    private Post post;

    public SchedulablePost(String snapshotFilePath, Post newPost) {
        this.snapshotFilePath  = snapshotFilePath;
        this.post = newPost;
    }
    @Override
    public void schedule(Context context, Date scheduledTime, Post newPost) throws Exception {
        //create dom and save as xml file
        Document postDOM =  XMLCreator.createPostXML(this);
        XMLCreator.saveLocalXMLFromDOM(postDOM,context.getFilesDir().getPath()+"/" + storageFilename);

    }

    @Override
    public void doWork() {
        FirebaseStorageRepository.getInstance()
                .uploadSnapshotFromLocal(this.snapshotFilePath, getContext().getFilesDir().getPath());
        PostRepository.getInstance().createOne(this.post);
    }

    @Override
    public void cancel() {

    }
    public DocumentReference getUid() {
        return uid;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }



}
