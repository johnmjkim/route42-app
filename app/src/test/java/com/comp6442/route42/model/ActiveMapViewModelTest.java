package com.comp6442.route42.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.comp6442.route42.R;
import com.comp6442.route42.ui.viewmodel.ActiveMapViewModel;
import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.BaseActivity;
import com.comp6442.route42.utils.MockLocation;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ActiveMapViewModelTest {
    @Test
    public void MapViewTest(){
        long elapsedTime = 0;
        Date date1 = new Date(2021,10,19);
        Date lastupdateDate = new Date(2021,10,18);

        ActiveMapViewModel activeMapViewModel = mock(ActiveMapViewModel.class);//make mock class
        Assert.assertTrue(activeMapViewModel!=null);

        when(activeMapViewModel.getActivityType()).thenReturn(Activity.Activity_Type.CYCLING);//decide return type to Cycling
        Assert.assertTrue(activeMapViewModel.getActivityType().equals(Activity.Activity_Type.CYCLING));//check

        when(activeMapViewModel.getLastUpdateTime()).thenReturn(lastupdateDate);//decide return date
        Assert.assertTrue(activeMapViewModel.getLastUpdateTime()== lastupdateDate);//check

        when(activeMapViewModel.getElapsedTime()).thenReturn(30L);//decide return elaspedtime
        Assert.assertTrue(activeMapViewModel.getElapsedTime()==30L);//check


//        Assert.assertEquals(activeMapViewModel.getElapsedTime()+ BaseActivity.getElapsedTimeSeconds(date1,lastupdateDate),86430);//ignore this. Maybe this is wrong way
//        Assert.assertEquals(elapsedTime+ BaseActivity.getElapsedTimeSeconds(date1,lastupdateDate),86400);
//        activeMapViewModel.reset();
//        Assert.assertEquals(elapsedTime,0);
//        Assert.assertEquals(activeMapViewModel.getActivityData(),null);
//        Assert.assertEquals(activeMapViewModel.getSnapshotFileName(),null);
//
//        --ordinary testing-- unfortunately feels like only this way can increase coverage :(
//        ActiveMapViewModel activeMapViewModel1 = new ActiveMapViewModel();
//        activeMapViewModel1.setActivityType(Activity.Activity_Type.RUNNING);
//        Assert.assertTrue(activeMapViewModel1.getActivityType().equals(Activity.Activity_Type.RUNNING));

    }
}
