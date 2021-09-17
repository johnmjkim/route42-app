package com.comp6442.route42.ui.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp6442.route42.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import timber.log.Timber;

public class MapsFragment extends Fragment {
  private static final String ARG_PARAM1 = "lat";
  private static final String ARG_PARAM2 = "lon";
  private Double lat, lon;
  private LatLng userLocation, imageLocation;


  private OnMapReadyCallback callback = new OnMapReadyCallback() {

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
      LatLng sydney = new LatLng(-34, 151);
      userLocation = new LatLng(-33.872789d, 151.205554d);
      imageLocation = new LatLng(lat, lon);

      Timber.i("Creating map. User location: (lat, lon) = %f, %f", imageLocation.latitude, imageLocation.longitude);
      googleMap.addMarker(new MarkerOptions().position(imageLocation).title("Marker in Sydney"));
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(imageLocation));
    }
  };

  public static MapsFragment newInstance(Double param1, Double param2) {
    Timber.i("New instance created with param (%f, %f)", param1, param2);
    MapsFragment fragment = new MapsFragment();
    Bundle args = new Bundle();
    args.putDouble(ARG_PARAM1, param1);
    args.putDouble(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      this.lat = getArguments().getDouble(ARG_PARAM1);
      this.lon = getArguments().getDouble(ARG_PARAM2);
    }
    Timber.i("Coordinate: (%f, %f)", lat, lon);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_maps, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

    if (savedInstanceState != null) {
      //Restore the fragment state
      this.lat = savedInstanceState.getDouble(ARG_PARAM1);
      this.lon = savedInstanceState.getDouble(ARG_PARAM2);
      Timber.i("Restoring fragment state (%f, %f)", lat, lon);
    }

    if (mapFragment != null) {
      mapFragment.getMapAsync(callback);
    }
  }
}