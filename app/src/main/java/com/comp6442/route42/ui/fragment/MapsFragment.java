package com.comp6442.route42.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.comp6442.route42.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import timber.log.Timber;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
  private static final String ARG_PARAM1 = "lat";
  private static final String ARG_PARAM2 = "lon";
  private Double lat, lon;
  private Location currentLocation;
  private LatLng userLocation;
  private LatLng imageLocation;
  private SupportMapFragment mapFragment;
  private GoogleMap googleMap;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private boolean locationPermissionGranted = false;
  private ActivityResultLauncher<String> requestPermissionLauncher;

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
    setHasOptionsMenu(true);

    // reveal bottom nav if hidden
    BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    bottomNavView.animate().translationY(0).setDuration(250);

    if (getArguments() != null) {
      this.lat = getArguments().getDouble(ARG_PARAM1);
      this.lon = getArguments().getDouble(ARG_PARAM2);
    }
    Timber.i("Coordinate: (%f, %f)", lat, lon);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_maps, container, false);
    init();
    getLocationPermission();
    return view;
  }

  public void init() {
    requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
      if (isGranted) {
        Timber.i("Location access granted");
        locationPermissionGranted = true;
        initializeMap();
      } else {
        Timber.w("Location access not granted");
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
          showAlert();
        } else {
          Snackbar snackbar = Snackbar.make(mapFragment.requireView(), "Permission not granted", Snackbar.LENGTH_INDEFINITE);
          locationPermissionGranted = false;
          snackbar.setAction("EXIT", view -> {
            requireActivity().finishAffinity();
            System.exit(0);
          });
          snackbar.show();
        }
      }
    });
  }

  private void initializeMap() {
    mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
    assert mapFragment != null;
    mapFragment.getMapAsync(this);
  }

  private void showAlert() {
    new AlertDialog.Builder(requireContext())
            .setTitle("Revoke permission")
            .setMessage("Enabling location access to Route42 will allow you to see your location relative to locations tagged by posts.")
            .setPositiveButton("OK", (dialog, which) -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION))
            .setNegativeButton("Cancel", (dialog, which) -> {
              Snackbar snackbar = Snackbar.make(
                      mapFragment.requireView(),
                      "Permission not granted",
                      Snackbar.LENGTH_INDEFINITE
              );

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationPermissionGranted = false;

                snackbar.setAction("EXIT", view -> {
                  requireActivity().finishAffinity();
                  System.exit(0);
                });
              } else {
                snackbar.setAction("REVOKE", view -> getLocationPermission());
              }
              snackbar.show();
              dialog.dismiss();
            }).create().show();
  }

  private void getLocationPermission() {
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationPermissionGranted = true;
      initializeMap();
    } else {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
  }

  private Task<Location> getDeviceLocation() {
    try {
      Timber.i("getDeviceLocation: getting the devices current location");
      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
      return locationPermissionGranted ? fusedLocationProviderClient.getLastLocation() : null;
    } catch (SecurityException e) {
      Timber.w("Unable to get current location");
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
      return null;
    } catch (RuntimeException e) {
      Timber.e(e);
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
      return null;
    }
  }

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
  public void onMapReady(@NonNull GoogleMap googleMap) {
    this.googleMap = googleMap;

    if (locationPermissionGranted) {
      if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
        return;
      }

      Snackbar snackbar = Snackbar.make(mapFragment.requireView(), "Please enable location access.", Snackbar.LENGTH_INDEFINITE);
      snackbar.setAction("REFRESH", view -> initializeMap());

      Task<Location> locationTask = getDeviceLocation();

      if (locationTask != null) {
        locationTask.addOnCompleteListener(
                task -> {
                  currentLocation = task.getResult();

                  if (task.isSuccessful() && currentLocation != null) {
                    renderMap(googleMap);
                  } else {
                    Timber.w("current location is null");
                    snackbar.show();
                  }
                });
      } else {
        Timber.w("location result is null");
        snackbar.show();
      }
    }
  }

  private void renderMap(@NonNull GoogleMap googleMap) {
    userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    imageLocation = new LatLng(lat, lon);

    Timber.i("Creating map. User location: (%f, %f) Image location: (%f, %f), (lat, lon)",
            userLocation.latitude,
            userLocation.longitude,
            imageLocation.latitude,
            imageLocation.longitude);

    googleMap.addMarker(new MarkerOptions().position(userLocation).title("User"));
    googleMap.addMarker(new MarkerOptions().position(imageLocation).title("Image"));
    googleMap.addPolyline(new PolylineOptions().add(userLocation, imageLocation).width(5).color(Color.RED));
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    googleMap.setMyLocationEnabled(true);
    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

    LatLngBounds bounds = new LatLngBounds.Builder()
            .include(userLocation)
            .include(imageLocation)
            .build();

    int padding = 300; // offset from edges of the map in pixels
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

    Handler handler = new Handler();
    handler.postDelayed(() -> googleMap.animateCamera(cameraUpdate), 1000);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mapFragment != null) mapFragment.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mapFragment != null) mapFragment.onResume();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (mapFragment != null) mapFragment.onDestroyView();
  }
}