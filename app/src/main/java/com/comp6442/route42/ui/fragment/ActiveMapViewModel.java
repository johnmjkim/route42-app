package com.comp6442.route42.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.MockLocation;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("MissingPermission")
public class ActiveMapViewModel extends ViewModel {

    private final MutableLiveData<Location> deviceLocation = new MutableLiveData<>();
    private final MockLocation mockLocations = new MockLocation(Activity.Activity_Type.RUNNING);
    private List<LatLng> pastLocations = new ArrayList<LatLng>();
    private Date startTime;
    private Activity activityData = null;



    private String snapshotFileName =  null;


    public ActiveMapViewModel() {
    }

    public LiveData<Location> getDeviceLocation() {
        return deviceLocation;
    }
    public void setDeviceLocation(Location newLocation) {
        if(deviceLocation.getValue() != null)
            addPastLocation(deviceLocation.getValue());
        deviceLocation.setValue(newLocation);
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setMockDeviceLocation() {
        Location location = new Location("");
        LatLng mockLoc = mockLocations.next();
        location.setLongitude(mockLoc.longitude);
        location.setLatitude(mockLoc.latitude);
        setDeviceLocation(location);
    }

    private void addPastLocation(Location location) {
        pastLocations.add(new LatLng(location.getLatitude(),location.getLongitude()));
    }

    public List<LatLng> getPastLocations() {
        return pastLocations;
    }
    public boolean hasPastLocations() {
        return pastLocations.size()>0;
    }

    public void setPastLocations(List<LatLng> pastLocations) {
        this.pastLocations = pastLocations;
    }

    public Activity getActivityData() {
        return activityData;
    }

    public void setActivityData(Activity activityData) {
        this.activityData = activityData;
    }
    public String getSnapshotFileName() {
        return snapshotFileName;
    }

    public void setSnapshotFileName(String snapshotFileName) {
        this.snapshotFileName = snapshotFileName;
    }
}