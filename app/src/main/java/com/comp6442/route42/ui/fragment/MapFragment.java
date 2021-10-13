package com.comp6442.route42.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import timber.log.Timber;

public abstract class MapFragment extends Fragment implements OnMapReadyCallback {

  protected abstract void renderMap() ;
  protected Location currentLocation = null;
  protected LatLng userLocation;
  protected SupportMapFragment mapFragment;
  protected GoogleMap googleMap;
  protected FusedLocationProviderClient fusedLocationProviderClient;
  protected boolean locationPermissionGranted = false;
  protected ActivityResultLauncher<String> requestPermissionLauncher;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    // reveal bottom nav if hidden
    BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    bottomNavView.animate().translationY(0).setDuration(250);
    try {
      Timber.i("getDeviceLocation: getting the devices current location");
      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    } catch (SecurityException e) {
      Timber.w("Unable to get current location");
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    } catch (RuntimeException e) {
      Timber.e(e);
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    }
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getLocationPermission();
  }

  protected void initializeMap() {
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

  protected void getLocationPermission() {
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

    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationPermissionGranted = true;
      initializeMap();
    } else {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
  }

  @SuppressLint("MissingPermission")
  protected Task<Location> getDeviceLocation() {
    return locationPermissionGranted ? fusedLocationProviderClient.getLastLocation() : null;
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera.
   * If Google Play services is not installed on the device, the user will be prompted to
   * install it inside the MapFragment. This method will only be triggered once the
   * user has installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(@NonNull GoogleMap googleMap) {
    Timber.i("Map ready. Beginning annotations.");
    this.googleMap = googleMap;

    Snackbar snackbar = Snackbar.make(
            mapFragment.requireView(),
            "Please enable location access.",
            Snackbar.LENGTH_INDEFINITE
    );
    snackbar.setAction("REFRESH", view -> initializeMap());

    Task<Location> locationTask = getDeviceLocation();

    if (locationTask != null) {
      locationTask.addOnCompleteListener(
              task -> {
                this.currentLocation = task.getResult();
                // Timber.i("User location: (%s)", currentLocation.toString());
                renderMap();
              });
    } else {
      snackbar.show();
    }
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