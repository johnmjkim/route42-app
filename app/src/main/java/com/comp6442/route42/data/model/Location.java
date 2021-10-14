package com.comp6442.route42.data.model;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    public static LatLng latLngFromLocation(android.location.Location location) {
        return  new LatLng(location.getLatitude(), location.getLongitude());
    }
}
