package com.comp6442.route42.data.model;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RunActivity implements Activity{
    private List<LatLng> route;
    private Float distance;
    private long elapsedTime;
    private Float speed;
    private int calories;
    private final int CALORIES_PER_METER = 4;
    private final Activity_Type activityType = Activity_Type.RUNNING;
    public RunActivity(List<LatLng> route, long elapsedTime) {
        this.route = route;
        this.distance = calculateDistance();
        this.elapsedTime = elapsedTime;
         this.calories = (int) (getDistance() * CALORIES_PER_METER);
        this.speed = distance / elapsedTime;
    }
    public static long getElapsedTimeSeconds(Date date1, Date date2) {
        return TimeUnit.SECONDS.convert(date2.getTime() - date1.getTime(), TimeUnit.MILLISECONDS);
    }
    public Float calculateDistance() {
        float total = 0.0F;
        if(route.size()>1) {
            for (int i=1; i<route.size() ; i++) {
                LatLng l1 = route.get(i);
                LatLng l2 = route.get(i-1);
                float[] p2pDistance = new float[3];
                Location.distanceBetween(l1.latitude, l1.longitude, l2.latitude,l2.longitude, p2pDistance);
                total += p2pDistance[0];
            }
        }

       return  total;
    }

    public int getCalories() {
        return calories;
    }

    public Float getDistance() {
        return distance;
    }

    public Float getSpeed() {
        return speed;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "Distance: " + distance +
                ", Duration:" + String.format("%.02f", (float) elapsedTime) +
                "\ncalories: " + calories
                ;
    }
    public String getPostString() {
        return "Check out my " + activityType.toString().toLowerCase() + " activity stats:\n" +
                "Distance: " + distance + "m" +
                "\nDuration: " + String.format("%.02f", (float) elapsedTime) + "s" +
                "\nAverage speed: " + speed + "m/s" +
                "\nCalories: " + calories + "kcal"
                ;
    }
}
