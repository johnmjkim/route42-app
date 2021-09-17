package com.comp6442.route42.ui.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp6442.route42.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import timber.log.Timber;

public class MapsFragment extends Fragment implements LocationListener {
  private static final String ARG_PARAM1 = "lat";
  private static final String ARG_PARAM2 = "lon";
  private Double lat, lon;
  private LatLng userLocation, imageLocation;

  private SupportMapFragment mapFragment;


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
      userLocation = new LatLng(-33.872789d, 151.205554d);
      imageLocation = new LatLng(lat, lon);

      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      builder.include(userLocation);
      builder.include(imageLocation);
      LatLngBounds bounds = builder.build();
      int padding = 50; // offset from edges of the map in pixels
      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

      googleMap.setBuildingsEnabled(true);
      googleMap.addMarker(new MarkerOptions().position(userLocation).title("User"));
      googleMap.addMarker(new MarkerOptions().position(imageLocation).title("Location of Image"));
      googleMap.addPolyline(new PolylineOptions().add(userLocation, imageLocation).width(5).color(Color.RED));
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

      Timber.i("Creating map. User location: (lat, lon) = %f, %f", imageLocation.latitude, imageLocation.longitude);
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        public void run() {
          googleMap.animateCamera(cameraUpdate);
//          googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(imageLocation, 15.0f));
        }
      }, 1000);
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
    mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

    // reveal bottom nav if hidden
    BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    bottomNavView.animate().translationY(0).setDuration(250);

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

  @Override
  public void onLocationChanged(@NonNull Location location) {
    Timber.i(location.toString());
  }

  @Override
  public void onPause() {
    super.onPause();
    mapFragment.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    mapFragment.onResume();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mapFragment.onDestroyView();
  }
}