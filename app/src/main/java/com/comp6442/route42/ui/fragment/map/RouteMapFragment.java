package com.comp6442.route42.ui.fragment.map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.comp6442.route42.R;
import com.comp6442.route42.data.model.Point;
import com.comp6442.route42.data.model.Post;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import timber.log.Timber;

public class RouteMapFragment extends MapFragment {
  private static final String ARG_PARAM1 = "post";
  private static final int LINE_COLOR = Color.rgb(255, 54, 54);
  private Post post;

  public RouteMapFragment() {
    super(R.id.map_fragment, R.raw.style_json);
  }

  public static PointMapFragment newInstance(Post param1) {
    PointMapFragment fragment = new PointMapFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARG_PARAM1, (Post) param1);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setHasOptionsMenu(true);
    if (getArguments() != null) this.post = getArguments().getParcelable(ARG_PARAM1);
    Timber.i("Creating map with post: %s", post);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_photo_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getLocationPermission();
  }

  protected void renderMap(Location location) {
    final int FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION);
    googleMap.setMyLocationEnabled(FINE_LOCATION_PERMISSION == PERMISSION_GRANTED);
    googleMap.getUiSettings().setMyLocationButtonEnabled(FINE_LOCATION_PERMISSION == PERMISSION_GRANTED);
    if (post != null) renderPost(post);
  }

  private void renderPost(Post post) {
    // add start and end marker, and draw lines
    LatLng prevLocation = post.getLatLng();
    LatLng currentLocation;

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    builder.include(prevLocation);
    googleMap.addMarker(new MarkerOptions().position(prevLocation).title("Start"));

    for (Point pt : post.getRoute().subList(1, post.getRoute().size())) {
      currentLocation = new LatLng(pt.getLatitude(), pt.getLongitude());
      googleMap.addPolyline(
              new PolylineOptions()
                      .add(prevLocation, currentLocation)
                      .width(5)
                      .color(LINE_COLOR)
      );
      builder.include(currentLocation);
      prevLocation = currentLocation;
    }
    googleMap.addMarker(new MarkerOptions().position(prevLocation).title("End"));

    // build bounds and move camera
    LatLngBounds bounds = builder.build();
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 300);
    googleMap.moveCamera(cameraUpdate);
  }
}