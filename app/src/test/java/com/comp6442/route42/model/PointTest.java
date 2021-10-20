package com.comp6442.route42.model;

import static org.mockito.Mockito.mock;

import android.os.Parcel;

import com.comp6442.route42.data.model.Point;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class PointTest {
    Point point,point1;
    @Mock
    Parcel parcelInput, parcelOutput;
    Double lat = -33.865;
    Double lon = 151.209;

    @Before
    public void setupTest() {
        point = new Point(lat, lon);
        point1 = new Point();
        parcelOutput = mock(Parcel.class);
        parcelInput = mock(Parcel.class);
        parcelInput.writeDoubleArray(new double[]{lat, lon});
    }

    @Test
    public void coordinateTest() {
        Assert.assertEquals(lat, point.getLatitude());
        Assert.assertEquals(lon, point.getLongitude());
    }

    @Test
    public void toStringTest() {
        Assert.assertEquals("Point{, latitude=-33.865, longitude=151.209}", point.toString());
    }

    @Test
    public void describeContentsTest() {
        Assert.assertEquals(0, point.describeContents());
    }

    @Test
    public void writeToParcelTest() {
        point.writeToParcel(parcelOutput, 0);
        Assert.assertEquals(parcelInput.readDouble(), parcelOutput.readDouble(),1e-8);
        Assert.assertEquals(parcelInput.readDouble(), parcelOutput.readDouble(),1e-8);
    }
}
