package com.comp6442.route42.data.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.*;

import timber.log.Timber;

public class MockLocation {
    private final List<LatLng> locations =Arrays.asList(
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
    private final List<Duration> locationTimes = new ArrayList<>(locations.size());
    private int currentIdx = 0;
    private int prevIdx;
    private final Instant startTime;
    public static LatLng latLngFromLocation(android.location.Location location) {
        return  new LatLng(location.getLatitude(), location.getLongitude());
    }
    public MockLocation(Activity.Activity_Type activityType) {
        double motorSpeed = 40.0;
        double cycleSpeed = 15.0;
        double walkSpeed = 3.0;
        double runSpeed = 9.0;
        double speed;
        switch (activityType) {
            case CYCLING:
                speed = cycleSpeed;
                break;
            case RUNNING:
                speed = runSpeed;
                break;
            case WALKING:
                speed = walkSpeed;
                break;
            default:
                speed = motorSpeed;
        }
        this.startTime = Instant.now();
        double totalDistance = 0.0;
        // initialize location times array
        for (int i=0; i<locations.size() ; i++) {
            if (i==0) {
                locationTimes.add( Duration.ofSeconds(0));
                continue;
            }
            LatLng l1 = locations.get(i);
            LatLng l2 = locations.get(i-1);
            float[] p2pDistance = new float[3];
            Location.distanceBetween(l1.latitude, l1.longitude, l2.latitude,l2.longitude, p2pDistance);
            totalDistance += p2pDistance[0];
            Duration elapsedTimeAtPoint =  Duration.ofSeconds((long) (totalDistance/ speed));
            locationTimes.add( elapsedTimeAtPoint);
        }
    }
    public LatLng next() {
        Instant currentTime = Instant.now();
        long elapsedSeconds =  currentTime.getEpochSecond() - startTime.getEpochSecond();
        while ( Duration.ofSeconds(elapsedSeconds).compareTo(locationTimes.get(currentIdx)) >=0) {
            Timber.i("YEEEE");
             prevIdx = currentIdx;
             currentIdx = (currentIdx % locations.size()) + 1;
        }
        Timber.i("elapsedSeocnds: " + elapsedSeconds);
        Timber.i("prevIdx: " + prevIdx);
        Timber.i("currentIdx: " + currentIdx);

        long nextPointElapsedSeconds = locationTimes.get(currentIdx).getSeconds();
        long previousPointElapsedSeconds = locationTimes.get(prevIdx).getSeconds();
        Timber.i("previous poiint elapsed secs" + previousPointElapsedSeconds);
        Timber.i("next poioint elapsed sec " + nextPointElapsedSeconds);
        float percentageBetweenConsecutivePts = (float)(elapsedSeconds - previousPointElapsedSeconds) / (nextPointElapsedSeconds - previousPointElapsedSeconds);
        Timber.i("percent btwn points " + percentageBetweenConsecutivePts);
        double longitude = locations.get(prevIdx).longitude + percentageBetweenConsecutivePts * (locations.get(currentIdx).longitude - locations.get(prevIdx).longitude);
        double latitude = locations.get(prevIdx).latitude + percentageBetweenConsecutivePts * (locations.get(currentIdx).latitude - locations.get(prevIdx).latitude);
        return new LatLng(latitude, longitude);
    }
}
