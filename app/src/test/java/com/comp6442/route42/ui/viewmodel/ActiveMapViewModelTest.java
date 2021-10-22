package com.comp6442.route42.ui.viewmodel;

import android.location.Location;

import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.BaseActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ActiveMapViewModelTest {

  final List<LatLng> pastLocation = Arrays.asList(//past location check
          new LatLng(-35.25932077515105, 149.11459641897002),
          new LatLng(-35.25918060533768, 149.11596970986986));
  final List<LatLng> newLocation = Arrays.asList(//past location check
          new LatLng(-35.25918060553768, 149.11596970996986));
  ActiveMapViewModel activeMapViewModel = new ActiveMapViewModel();
  long elapsedTime = 0;
  Date date1 = new Date(2021, 10, 19);
  Date date2 = new Date(2021, 10, 20);
  Date lastUpdateTime = new Date(2021, 10, 18);
  Activity activity;
  Location location;

  @Before
  public void setUp() {
    activeMapViewModel.setPastLocations(pastLocation);
    activeMapViewModel.setSnapshotFileName("TestLocation");//filename check
    activeMapViewModel.setLastUpdateTime(date1);//updatedate check
    activity = Mockito.mock(Activity.class);
    location = Mockito.mock(Location.class);
  }

  @Test
  public void typeTest() {
    activeMapViewModel.setActivityType(Activity.Activity_Type.RUNNING);//type check
    Assert.assertEquals(Activity.Activity_Type.RUNNING, activeMapViewModel.getActivityType());
    activeMapViewModel.setActivityType(Activity.Activity_Type.CYCLING);//type check
    Assert.assertEquals(Activity.Activity_Type.CYCLING, activeMapViewModel.getActivityType());
    activeMapViewModel.setActivityType(Activity.Activity_Type.WALKING);//type check
    Assert.assertEquals(Activity.Activity_Type.WALKING, activeMapViewModel.getActivityType());
  }

  @Test
  public void elapsedTimeTest() {
    Assert.assertEquals(0, activeMapViewModel.getElapsedTime());
    elapsedTime = elapsedTime + BaseActivity.getElapsedTimeSeconds(date2, lastUpdateTime);
    Assert.assertEquals(172800, elapsedTime);
  }

  @Test
  public void lastUpdateTimeTest() {
    Assert.assertSame(date1, activeMapViewModel.getLastUpdateTime());
  }

  @Test
  public void resetTest() {
    activeMapViewModel.reset();//reset check
    Assert.assertEquals(0, activeMapViewModel.getElapsedTime());
    Assert.assertNull(activeMapViewModel.getLastUpdateTime());
    Assert.assertEquals(new ArrayList<>(), activeMapViewModel.getPastLocations());
    Assert.assertNull(activeMapViewModel.getSnapshotFileName());
    Assert.assertNull(activeMapViewModel.getActivityData());
    Assert.assertNull(activeMapViewModel.getActivityType());
    Assert.assertFalse(activeMapViewModel.hasPastLocations());
  }

  @Test
  public void pastLocationTest() {
    Assert.assertTrue(activeMapViewModel.hasPastLocations());
    Assert.assertEquals(pastLocation, activeMapViewModel.getPastLocations());
  }

  @Test
  public void snapShotFileNameTest() {
    Assert.assertEquals("TestLocation", activeMapViewModel.getSnapshotFileName());
  }

  @Test
  public void activityTest() {
    activeMapViewModel.setActivityData(activity);
    Assert.assertEquals(activity, activeMapViewModel.getActivityData());
  }
}
