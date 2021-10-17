package com.comp6442.route42.data.model;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.comp6442.route42.utils.XMLCreator;
import com.comp6442.route42.utils.tasks.ScheduledTask;

import org.w3c.dom.Document;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.TransformerException;

import timber.log.Timber;

public class SchedulablePost implements  Schedulable<Post>{
    public static CharSequence[] delayOptions = new CharSequence[]{"1", "5", "30", "60"};
    private UUID workId = null;
    private WorkManager workManager = null;
    private final String baseFilename = "scheduled_post";
    private final String storageFilename = baseFilename + new Date().toString() + ".xml";
    private final String snapshotFilePath ;
    private final String snapshotFilename;
    private final String uid;
    private final String userName;
    private final int isPublic;
    private final String profilePicUrl;
    private final String postDescription ;
    private final String locationName;
    private final Double latitude;
    private final Double longitude;

    public SchedulablePost(String snapshotFilePath, String snapshotFilename, String uid, String userName, int isPublic, String profilePicUrl, String postDescription, String locationName, Double latitude, Double longitude) {
        this.snapshotFilePath = snapshotFilePath;
        this.snapshotFilename = snapshotFilename;
        this.uid = uid;
        this.userName = userName;
        this.isPublic = isPublic;
        this.profilePicUrl = profilePicUrl;
        this.postDescription = postDescription;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void schedule(Context context, int scheduledDelay)  {
        //create dom and save as xml file
        try{
            Timber.i("Trying");
            Document postDOM =  XMLCreator.createPostXML(this);
            XMLCreator.saveLocalXMLFromDOM(postDOM,context.getFilesDir().getPath()  + "/" + storageFilename);
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(ScheduledTask.class)
                    .setInitialDelay(scheduledDelay, TimeUnit.MINUTES)
                    .setInputData(
                            new Data.Builder()
                                    .putString("type", "activity_post")
                                    .putString("snapshotFilePath", this.snapshotFilePath)
                                    .putString("snapshotFilename", this.snapshotFilename)
                                    .putString("uid", uid)
                                    .putString("username", userName)
                                    .putInt("isPublic", isPublic)
                                    .putString("profilePicUrl" , profilePicUrl)
                                    .putString("postDescription", postDescription)
                                    .putString("locationName",  locationName)
                                    .putDouble("latitude" , latitude)
                                    .putDouble("longitude" , longitude)
                                    .build()
                    )
                    .build();
            workId =  workRequest.getId();
            workManager =  WorkManager.getInstance(context);
            workManager.enqueue(workRequest);
        } catch (TransformerException e) {

        } catch (Exception e) {

        }

    }


    @Override
    public void cancel() {
        if(workId != null && workManager != null) {
            Operation cancelWorkOperation =  workManager.cancelWorkById(workId);
            cancelWorkOperation.getResult();
        }
    }



}
