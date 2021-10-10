package com.comp6442.route42.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.MockLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.geometry.LatLng;

@SuppressLint("MissingPermission")
public class ActiveMapViewModel extends ViewModel {


    // TODO: Implement the ViewModel
  private final MutableLiveData<Location> deviceLocation = new MutableLiveData<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MockLocation mockLocations = new MockLocation(Activity.Activity_Type.RUNNING);

    public LiveData<Location> getDeviceLocation() {
     setMockLocation();
     return deviceLocation;
    };

    public void setMockLocation() {
        Location location = new Location("");
        LatLng mock = mockLocations.next();
        location.setLongitude(mock.getLongitude());
        location.setLatitude(mock.getLatitude());
        fusedLocationProviderClient.setMockLocation(location);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(deviceLocation::setValue);
    }
    public void setFusedLocationProviderClient(Context fragmentContext) {
        // request access to fine location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragmentContext);
        fusedLocationProviderClient.setMockMode(true);
    }

}