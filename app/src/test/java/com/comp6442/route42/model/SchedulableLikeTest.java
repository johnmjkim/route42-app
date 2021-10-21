package com.comp6442.route42.model;

import static org.mockito.Mockito.mock;

import android.content.Context;

import com.comp6442.route42.data.model.ScheduleableLike;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchedulableLikeTest {
    private final String uid = "d259b635-1d89-482f-82e5-686582d38cea";
    private final String postId = "Testuser";
    ScheduleableLike scheduleableLike =  new ScheduleableLike(uid,postId);
    Context context = mock(Context.class);

    @Before
    public void scheduleLike(){
        scheduleableLike.schedule(context,30);
    }
//    @Test
//    public void test1(){
//        not sure what we can test on this
//    }


    @After
    public void cancel(){
        scheduleableLike.cancel();
    }



}
