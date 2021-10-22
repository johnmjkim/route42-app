package com.comp6442.route42.utils.tasks.scheduled_tasks;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.comp6442.route42.data.model.Point;
import com.comp6442.route42.utils.xmlresource.PostXMLCreator;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class PostScheduler implements Scheduler {
    private UUID workId = null;
    private WorkManager workManager = null;
    private final String baseFilename = "scheduled_post";
    private final String storageFilename = baseFilename + "_" + new Timestamp(System.currentTimeMillis()).getTime() + ".xml";
    private final String snapshotFilePath;
    private final String snapshotFilename;
    private final String uid;
    private final String userName;
    private final int isPublic;
    private final List<Point> route;
    private final String profilePicUrl;
    private final String postDescription;
    private final String locationName;
    private final Double latitude;
    private final Double longitude;

    public PostScheduler(String snapshotFilePath, String snapshotFilename, String uid, String userName, int isPublic, String profilePicUrl, String postDescription, List<Point> route, String locationName, Double latitude, Double longitude) {
        this.snapshotFilePath = snapshotFilePath;
        this.snapshotFilename = snapshotFilename;
        this.uid = uid;
        this.userName = userName;
        this.isPublic = isPublic;
        this.profilePicUrl = profilePicUrl;
        this.postDescription = postDescription;
        this.route = route;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public int getIsPublic() {
        return isPublic;
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

    public List<Point> getRoute() {
        return route;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public void schedule(Context context, int scheduledDelay) {
        try {
            String xmlFilePath = context.getFilesDir().getPath() + "/" + storageFilename;
            //create dom and save as xml file
            PostXMLCreator.create(this, xmlFilePath);
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(ScheduledTask.class)
                    .setInitialDelay(scheduledDelay, TimeUnit.MINUTES)
                    .setInputData(
                            new Data.Builder()
                                    .putString("type", "activity_post")
                                    .putString("snapshotFilePath", this.snapshotFilePath)
                                    .putString("snapshotFilename", this.snapshotFilename)
                                    .putString("xmlFilePath", xmlFilePath)
                                    .build()
                    )
                    .build();
            workId = workRequest.getId();
            workManager = WorkManager.getInstance(context);
            workManager.enqueue(workRequest);
            Timber.i("scheduled work request");
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    @Override
    public void cancel() {
        if (workId != null && workManager != null) {
            Operation cancelWorkOperation = workManager.cancelWorkById(workId);
            cancelWorkOperation.getResult();
        }
    }


}
