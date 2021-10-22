package com.comp6442.route42.data.model;

import android.os.Parcel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class PointTest {
  Point point, pointnull;
  @Mock
  Parcel parcelInput, parcelOutput;
  Double lat = -33.865;
  Double lon = 151.209;

  @Before
  public void setupTest() {
    point = new Point(lat, lon);
    pointnull = new Point(null, null);
    parcelOutput = Mockito.mock(Parcel.class);
    parcelInput = Mockito.mock(Parcel.class);
    parcelInput.writeDoubleArray(new double[]{lat, lon});
  }

  @Test
  public void coordinateTest() {
    Assert.assertEquals(lat, point.getLatitude());
    Assert.assertEquals(lon, point.getLongitude());
  }

  @Test
  public void toStringTest() {
    Assert.assertEquals("Point{, latitude=" +
            lat +
            ", longitude=" +
            lon +
            "}", point.toString());
  }

  @Test
  public void describeContentsTest() {
    Assert.assertEquals(0, point.describeContents());
  }

  @Test
  public void writeToParcelTest() {
    point.writeToParcel(parcelOutput, 0);
    Assert.assertEquals(parcelInput.readDouble(), parcelOutput.readDouble(), 1e-8);
    Assert.assertEquals(parcelInput.readDouble(), parcelOutput.readDouble(), 1e-8);
    pointnull.writeToParcel(parcelOutput, 0);
    Assert.assertEquals(parcelInput.readDouble(), parcelOutput.readDouble(), 1e-8);
    Assert.assertEquals(parcelInput.readDouble(), parcelOutput.readDouble(), 1e-8);
  }
}
