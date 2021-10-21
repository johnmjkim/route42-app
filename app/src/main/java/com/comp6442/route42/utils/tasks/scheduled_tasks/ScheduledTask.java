package com.comp6442.route42.utils.tasks.scheduled_tasks;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.utils.xmlresource.PostXMLParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

import timber.log.Timber;

public class ScheduledTask extends Worker {
    public ScheduledTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    private class PostIDUserID {
        public final String uid;
        public final String postid;
        private PostIDUserID(String uid, String postid) {
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
                String xmlPath = getInputData().getString("xmlFilePath");
                // upload snapshot
                String imageUrl =  FirebaseStorageRepository.getInstance()
                        .uploadSnapshotFromLocal(getInputData().getString("snapshotFilePath"),
                                getInputData().getString("snapshotFilename") )
                        .getPath();
                // create new post object
                Post newPost = new PostXMLParser().parse(xmlPath);
                newPost.setImageUrl(imageUrl);
                // upload post to database storage
                PostRepository.getInstance().createOne(newPost);
                //delete local file
                File fileObj = new File(Objects.requireNonNull(xmlPath));
                if (!fileObj.delete()) {
                    Timber.w("xml file is not deleted at %s", xmlPath);
                }

            } else if(workType.equals("like_post")) {
                PostIDUserID data = parseLikeFile(getInputData().getString("like_data_filepath"));
                assert data != null;
                PostRepository.getInstance().scheduleLike(data.postid, data.uid);
            }
        }
        return Result.success();
    }

    /**
     * Utility method for doWork();
     * parses text file containing post and user information for the Like.
     * @param filePath file path to text file
     * @return object with postID and userID
     */
    private PostIDUserID parseLikeFile(String filePath) {
        File fileObj = new File(filePath);
        try {
            if (fileObj.exists()) {
                Scanner reader = new Scanner(fileObj);
                String data = reader.nextLine();
                String[] pair = data.split(",");
                reader.close();
                boolean fileIsDeleted = fileObj.delete();
                if (!fileIsDeleted) {
                    Timber.w("file is not deleted at %s", filePath);
                }
                return new PostIDUserID(pair[0], pair[1]);

            } else throw new FileNotFoundException("File not found at " + filePath);
        } catch (FileNotFoundException e) {
            Timber.e(e);
            return null;
        }
    }
}
