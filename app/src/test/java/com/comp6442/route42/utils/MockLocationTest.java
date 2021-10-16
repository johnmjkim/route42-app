package com.comp6442.route42.utils;

import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.MockLocation;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.*;

import timber.log.Timber;

public class MockLocationTest {
    public MockLocation mock;

    @Test
    public void cycleTest(){
        mock = new MockLocation(Activity.Activity_Type.CYCLING);
    }

    @Test
    public void runningTest(){
        mock = new MockLocation(Activity.Activity_Type.RUNNING);
    }

    @Test
    public void walkingTest(){
        mock = new MockLocation(Activity.Activity_Type.WALKING);
    }
}