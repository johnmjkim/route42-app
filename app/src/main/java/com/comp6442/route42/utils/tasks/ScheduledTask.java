package com.comp6442.route42.utils.tasks;

import static com.comp6442.route42.data.model.Post.getHashTagsFromTextInput;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import timber.log.Timber;

public class ScheduledTask extends Worker {
    public ScheduledTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    private class PostUserPair {
        public final String uid;
        public final String postid;
        private PostUserPair(String uid, String postid) {
            this.uid = uid;
            this.postid = postid;
        }
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

            } else if(workType.equals("like_post")) {
                PostUserPair data = parseLikeFile(getInputData().getString("like_data_filepath"));
                assert data != null;
                PostRepository.getInstance().scheduleLike(data.postid, data.uid);
            }

        }

        return Result.success();
    }
    private PostUserPair parseLikeFile(String filePath) {
        File myObj = new File(filePath);
        try {
            if(myObj.exists() ) {
                Scanner reader = new Scanner(myObj);
                String data = reader.nextLine();
                String[] pair = data.split(",");
                reader.close();
                return new PostUserPair(pair[0],pair[1]);

            } else throw new FileNotFoundException("File not found at "+ filePath);
        }
        catch (FileNotFoundException e) {
            Timber.e(e);
            return null;
        }
    }
}
