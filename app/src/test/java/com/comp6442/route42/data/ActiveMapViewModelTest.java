package com.comp6442.route42.data;

import com.comp6442.route42.ui.viewmodel.ActiveMapViewModel;
import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.BaseActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ActiveMapViewModelTest {
    @Test
    public void MapViewTest(){
        long elapsedTime = 0;
        Date date1 = new Date(2021,10,19);
        Date date2 = new Date(2021,10,20);
        Date lastUpdateTime = new Date(2021,10,18);

        ActiveMapViewModel activeMapViewModel = new ActiveMapViewModel();
        activeMapViewModel.setActivityType(Activity.Activity_Type.RUNNING);//type check
        Assert.assertTrue(activeMapViewModel.getActivityType().equals(Activity.Activity_Type.RUNNING));
        Assert.assertTrue(activeMapViewModel.getElapsedTime()==0);

        activeMapViewModel.setLastUpdateTime(date1);//updatedate check
        Assert.assertTrue(activeMapViewModel.getLastUpdateTime()==date1);
        //activeMapViewModel.updateElapsedTime(); //do this below instead of using the method because original method use new date insted of date2
        elapsedTime = elapsedTime + BaseActivity.getElapsedTimeSeconds(date2, lastUpdateTime);
        Assert.assertEquals(elapsedTime,172800);

        activeMapViewModel.reset();//reset check
        Assert.assertEquals(activeMapViewModel.getElapsedTime(),0);
        Assert.assertEquals(activeMapViewModel.getLastUpdateTime(),null);
        Assert.assertEquals(activeMapViewModel.getPastLocations(),new ArrayList<>());
        Assert.assertEquals(activeMapViewModel.getSnapshotFileName(),null);
        Assert.assertEquals(activeMapViewModel.getActivityData(),null);
        Assert.assertEquals(activeMapViewModel.getActivityType(),null);
        Assert.assertEquals(activeMapViewModel.hasPastLocations(),false);

        final List<LatLng> pastLocation = Arrays.asList(//past location check
                new LatLng(-35.25932077515105, 149.11459641897002),
                new LatLng(-35.25918060533768, 149.11596970986986));
        activeMapViewModel.setPastLocations(pastLocation);
        Assert.assertEquals(activeMapViewModel.hasPastLocations(),true);
        Assert.assertEquals(activeMapViewModel.getPastLocations(),pastLocation);

        activeMapViewModel.setSnapshotFileName("TestLocation");//filename check
        Assert.assertEquals(activeMapViewModel.getSnapshotFileName(),"TestLocation");
    }
}
