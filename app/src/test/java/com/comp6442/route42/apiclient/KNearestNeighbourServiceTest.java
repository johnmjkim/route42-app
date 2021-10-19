package com.comp6442.route42.apiclient;

import com.comp6442.route42.api.KNearestNeighbourService;
import com.comp6442.route42.data.model.Post;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import timber.log.Timber;

public class KNearestNeighbourServiceTest {
    int k = 5;
    Double lat = -35.25932077515105;
    Double lon = 149.11459641897002;
    KNearestNeighbourService kNearestNeighbourService = new KNearestNeighbourService(k, lat, lon);

    @Test
    public void callTest() throws Exception {
        Assert.assertEquals(null, kNearestNeighbourService.call());
    }

    @Test
    public void toStringTest() {
        Assert.assertEquals("KNearestNeighbourService{k=5, lat=-35.25932077515105, lon=149.11459641897002}", kNearestNeighbourService.toString());
    }
}
