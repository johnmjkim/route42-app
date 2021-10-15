package com.comp6442.route42.data;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.BaseActivity;
import com.comp6442.route42.utils.MockLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActiveMapViewModel extends ViewModel {

    private  MutableLiveData<Location> deviceLocation = new MutableLiveData<>();
    private MockLocation mockLocations = new MockLocation(Activity.Activity_Type.RUNNING);
    private List<LatLng> pastLocations = new ArrayList<>();
    private Activity activityData = null;
    private String snapshotFileName =  null;
    private long elapsedTime = 0;

    public Activity.Activity_Type getActivityType() {
        return activityType;
    }

    public void setActivityType(Activity.Activity_Type activityType) {
        this.activityType = activityType;
    }

    private Activity.Activity_Type activityType = null;
    private Date lastUpdateTime = null;

    public ActiveMapViewModel() {
    }
    /**
     * Resets the data collected within an active map fragment.
     */
    public void reset() {
        deviceLocation = new MutableLiveData<>();
        mockLocations = new MockLocation(Activity.Activity_Type.RUNNING);
        pastLocations = new ArrayList<>();
        activityData = null;
        snapshotFileName =  null;
        elapsedTime = 0;
        lastUpdateTime = null;
        activityType = null;
    }
    public long getElapsedTime() {
        return elapsedTime;
    }

    public void updateElapsedTime() {
        this.elapsedTime = elapsedTime + BaseActivity.getElapsedTimeSeconds(new Date(), lastUpdateTime);
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(@Nullable Date time) {
        this.lastUpdateTime = time;
    }

    public LiveData<Location> getDeviceLocation() {
        return deviceLocation;
    }
    public void setDeviceLocation(Location newLocation) {
        if(deviceLocation.getValue() != null)
            addPastLocation(deviceLocation.getValue());
        deviceLocation.setValue(newLocation);
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