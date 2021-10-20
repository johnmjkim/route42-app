package com.comp6442.route42.model;

import static org.mockito.Mockito.mock;

import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.BaseActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.type.Date;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BaseActivityTest {
    private final List<LatLng> locations = Arrays.asList(
            new LatLng(-35.25932077515105, 149.11459641897002),
            new LatLng(-35.25918060533768, 149.11596970986986),
            new LatLng(-35.25939085996682, 149.11824422292267),
            new LatLng(-35.26096775229834, 149.11790090019772),
            new LatLng(-35.2608626270975, 149.11627011725417),
            new LatLng(-35.26159850063965, 149.11446767294817),
            new LatLng(-35.26156345919392, 149.11425309624508),
            new LatLng(-35.26447184762463, 149.1140814348826),
            new LatLng(-35.26468208852564, 149.1156263871449),
            new LatLng(-35.26464704841336, 149.11863046098824),
            new LatLng(-35.25942590235197, 149.11978917518496),
            new LatLng(-35.264436807421426, 149.11910252973504),
            new LatLng(-35.26506752876069, 149.12223534960026),
            new LatLng(-35.259636156344726, 149.12326531777512),
            new LatLng(-35.26019683099199, 149.12807183592446),
            new LatLng(-35.26534784777999, 149.12266450300643),
            new LatLng(-35.26552304667459, 149.1274710211558),
            new LatLng(-35.26009170479109, 149.12871556603378),
            new LatLng(-35.26058229256188, 149.13090424840533),
            new LatLng(-35.26089766884627, 149.1360970046203),
            new LatLng(-35.26653919279271, 149.13515286712666),
            new LatLng(-35.26601360156408, 149.13000302625235),
            new LatLng(-35.260617334431856, 149.13086133306473),
            new LatLng(-35.265768324490594, 149.1301746876148),
            new LatLng(-35.26114296066338, 149.135453274511)
    );
    @Test
    public void cyclingTest() throws ParseException {

        BaseActivity cyclingBase = new BaseActivity(locations,10, Activity.Activity_Type.CYCLING);
        Assert.assertEquals(0.0, cyclingBase.getCalories(),0.00001);
        Assert.assertEquals(0.0, cyclingBase.getDistance(),0.00001);
        Assert.assertEquals(0.0, cyclingBase.getSpeed(),0.00001);
        Assert.assertEquals(
                "Check out my cycling activity stats:\n" +
                "Distance: 0.0 m\n" +
                "Duration: 10 s\n" +
                "Average speed: 0.0 m/s\n" +
                "Calories: 0 kcal\n" +
                " #myworkouts", cyclingBase.getPostString());

        Assert.assertEquals("Distance: 0m\n"+"Duration:10.0s\n"+"Calories: 0", cyclingBase.toString());
        String expiryDateString = "2018-10-15T17:52:00";
        String expiryDateString1 = "2018-10-15T19:52:00";
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        java.util.Date date = formatter.parse(expiryDateString);
        java.util.Date date1 = formatter.parse(expiryDateString1);
        Assert.assertEquals(7200, BaseActivity.getElapsedTimeSeconds(date1,date));
    };
}
