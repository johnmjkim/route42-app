package com.comp6442.route42.utils.tasks;

import static com.comp6442.route42.data.model.Post.getHashTagsFromTextInput;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.Schedulable;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;

public class ScheduledTask extends Worker {
    public ScheduledTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if(getInputData().getKeyValueMap().containsKey("type")) {
            String workType = getInputData().getString("type");
            assert workType != null;
            if(workType.equals("activity_post")) {
                // upload snapshot
                String imageUrl =  FirebaseStorageRepository.getInstance()
                        .uploadSnapshotFromLocal(getInputData().getString("snapshotFilePath"),
                                getInputData().getString("snapshotFilename") )
                        .getPath();
                // create new post object
                Post newPost = new Post( UserRepository.getInstance().getOne(getInputData().getString("uid")),
                        getInputData().getString("username"),
                        getInputData().getInt("isPublic", 0),
                        getInputData().getString("profilePicUrl"),
                        new Date(),
                        getInputData().getString("postDescription"),
                        "",
                        getInputData().getDouble("latitude", 0.0),
                        getInputData().getDouble("longitude", 0.0),
                        getHashTagsFromTextInput( getInputData().getString("postDescription")),
                        0,
                        imageUrl,
                        new ArrayList<>(0));
                // upload post to database storage
                PostRepository.getInstance().createOne(newPost);
            }

        }

        return Result.success();
    }
}
