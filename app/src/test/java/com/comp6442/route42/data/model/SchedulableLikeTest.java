package com.comp6442.route42.data.model;

import android.content.Context;

import com.comp6442.route42.utils.tasks.scheduled_tasks.LikeScheduler;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

public class SchedulableLikeTest {
  private final String uid = "d259b635-1d89-482f-82e5-686582d38cea";
  private final String postId = "Testuser";
  LikeScheduler scheduleableLike = new LikeScheduler(uid, postId);
  Context context = Mockito.mock(Context.class);

  @Before
  public void scheduleLike() {
    scheduleableLike.schedule(context, 30);
  }
//    @Test
//    public void test1(){
//        not sure what we can test on this
//    }


  @After
  public void cancel() {
    scheduleableLike.cancel();
  }


}
