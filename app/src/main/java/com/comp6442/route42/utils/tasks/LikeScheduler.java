package com.comp6442.route42.utils.tasks;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.comp6442.route42.utils.tasks.ScheduledTask;
import com.comp6442.route42.utils.tasks.Scheduler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class LikeScheduler implements Scheduler {
    private UUID workId = null;
    private WorkManager workManager = null;
    private final String uid;
    private final String postId;

    public LikeScheduler(String uid, String postId) {
        this.uid = uid;
        this.postId = postId;
    }

    @Override
    public void schedule(Context context, int scheduledDelay) {
        try {
            String baseFilename = "scheduled_like";
            String storageFilename = baseFilename + ".txt";
            FileOutputStream outputStream = context.openFileOutput(storageFilename, 0);
            String writeString = uid + "," + postId;
            outputStream.write(writeString.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(ScheduledTask.class)
                    .setInitialDelay(scheduledDelay, TimeUnit.MINUTES)
                    .setInputData(
                            new Data.Builder()
                                    .putString("type", "like_post")
                                    .putString("like_data_filepath", context.getFilesDir().getPath() + "/" + storageFilename)
                                    .build()
                    )
                    .build();
            workId = workRequest.getId();
            workManager = WorkManager.getInstance(context);
            workManager.enqueue(workRequest);
        } catch (IOException e) {
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
