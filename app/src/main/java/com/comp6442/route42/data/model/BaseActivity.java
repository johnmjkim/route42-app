package com.comp6442.route42.data.model;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BaseActivity implements Activity{
    private List<LatLng> route;
    private Float distance;
    private long elapsedTime;
    private Float speed;
    private int calories;
    private final int CALORIES_PER_METER = 2;
    private final Activity_Type activityType = Activity_Type.RUNNING;

    public static long getElapsedTimeSeconds(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime()) / 1000 ;
    }
    public BaseActivity(List<LatLng> route, long elapsedTime) {
        this.route = route;
        this.distance = calculateDistance();
        this.elapsedTime = elapsedTime;
         this.calories = (int) (getDistance() * CALORIES_PER_METER);
        this.speed = distance / elapsedTime;
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

    private Float calculateDistance() {
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

    @NonNull
    @Override
    @SuppressLint("DefaultLocale")
    public String toString() {
        return
                "Distance: " + String.format("%.00f", distance) +  "m"+
                "\nDuration:" + String.format("%.01f", (float) elapsedTime) + "s"+
                "\nCalories: " + calories
                ;
    }
    @SuppressLint("DefaultLocale")
    public String getPostString() {
        return "Check out my " + activityType.toString().toLowerCase() + " activity stats:\n" +
                "Distance: " + String.format("%.01f", distance) + " m" +
                "\nDuration: " + String.format("%.00f", (float) elapsedTime) + " s" +
                "\nAverage speed: " + String.format("%.01f",  speed)  + " m/s" +
                "\nCalories: " + calories + " kcal" +
                "\n #myworkouts"

                ;
    }
}
