package com.comp6442.route42.apiclient;

import com.comp6442.route42.api.KNearestNeighbourService;

import org.junit.Assert;
import org.junit.Test;

public class KNearestNeighbourServiceTest {
  int k = 5;
  Double lat = -35.25932077515105;
  Double lon = 149.11459641897002;
  KNearestNeighbourService kNearestNeighbourService = new KNearestNeighbourService(k, lat, lon);

  @Test
  public void callTest() throws Exception {
    Assert.assertNull(kNearestNeighbourService.call());
  }

  @Test
  public void getValues() {
    Assert.assertEquals(k, kNearestNeighbourService.getK());
    Assert.assertEquals(lat, kNearestNeighbourService.getLat());
    Assert.assertEquals(lon, kNearestNeighbourService.getLon());
  }

  @Test
  public void toStringTest() {
    Assert.assertTrue(kNearestNeighbourService.toString().contains(String.valueOf(k)));
    Assert.assertTrue(kNearestNeighbourService.toString().contains(String.valueOf(lat)));
    Assert.assertTrue(kNearestNeighbourService.toString().contains(String.valueOf(lon)));
    Assert.assertEquals("KNearestNeighbourService{k=" +
            k +
            ", lat=" +
            lat +
            ", lon=" +
            lon +
            "}", kNearestNeighbourService.toString());
  }
}
