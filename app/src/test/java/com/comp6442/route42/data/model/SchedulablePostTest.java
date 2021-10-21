package com.comp6442.route42.data.model;

import static org.mockito.Mockito.mock;

import android.content.Context;

import androidx.work.WorkManager;

import com.comp6442.route42.utils.tasks.scheduled_tasks.PostScheduler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.UUID;

public class SchedulablePostTest {

    private UUID workId = null;
    private WorkManager workManager = null;
    private final String baseFilename = "scheduled_post";
    private final String storageFilename = baseFilename + "_" + new Timestamp(System.currentTimeMillis()).getTime() + ".xml";
    private final String snapshotFilePath = "C:\\Users";
    private final String snapshotFilename = "1minDelayPost";
    private final String uid = "d259b635-1d89-482f-82e5-686582d38cea";
    private final String userName = "Paul";
    private final int isPublic = 0;
    private final String profilePicUrl = "https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400";
    private final String postDescription = "Station";
    private final String locationName = "Sydney";
    private final Double latitude = -33.865;
    private final Double longitude = 151.209;
    Context context = mock(Context.class);
    PostScheduler schedulablePost = new PostScheduler(snapshotFilePath, snapshotFilename, uid, userName, isPublic, profilePicUrl, postDescription, locationName, latitude, longitude);

    @Before
    public void setUp() {
        schedulablePost.schedule(context, 30);
    }

    @Test
    public void checkLatitude() {
        Assert.assertEquals(schedulablePost.getLatitude(), -33.865, 0.001);
    }

    @Test
    public void checkLongitute() {
        Assert.assertEquals(schedulablePost.getLongitude(), 151.209, 0.001);
    }

    @Test
    public void checkIsPublic() {
        Assert.assertEquals(schedulablePost.getIsPublic(), 0);
    }

    @Test
    public void checkPostdescription() {
        Assert.assertEquals(schedulablePost.getPostDescription(), "Station");
    }

    @Test
    public void checkLocationName() {
        Assert.assertEquals(schedulablePost.getLocationName(), "Sydney");
    }

    @Test
    public void checkProfileUrl() {
        Assert.assertEquals(schedulablePost.getProfilePicUrl(), "https://images.unsplash.com/photo-1631515998707-f54897e89a68?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODIzNTM&ixlib=rb-1.2.1&q=80&w=400");
    }

    @Test
    public void checkUserName() {
        Assert.assertEquals(schedulablePost.getUserName(), "Paul");
    }

    @Test
    public void checkUid() {
        Assert.assertEquals(schedulablePost.getUid(), "d259b635-1d89-482f-82e5-686582d38cea");
    }

    @After
    public void cancel() {
        schedulablePost.cancel();
    }
}
