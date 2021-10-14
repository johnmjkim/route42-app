package com.comp6442.route42.ui.fragment;

import android.Manifest;
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
import androidx.fragment.app.FragmentActivity;

import com.comp6442.route42.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import timber.log.Timber;

public abstract class MapFragment extends Fragment implements OnMapReadyCallback {
  protected int MAP_FRAGMENT;
  protected int MAP_STYLE;
  protected SupportMapFragment mapFragment;
  protected GoogleMap googleMap;
  protected FusedLocationProviderClient fusedLocationProviderClient;
  protected ActivityResultLauncher<String> requestPermissionLauncher;

  public MapFragment(int mapFragment, int mapStyle) {
    this.MAP_FRAGMENT = mapFragment;
    this.MAP_STYLE = mapStyle;
  }

  protected abstract void renderMap(Location location);

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    // reveal bottom nav if hidden
    BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    bottomNavView.animate().translationY(0).setDuration(250);
    requireActivity().findViewById(R.id.Btn_Create_Activity).setVisibility(View.INVISIBLE);
    try {
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
    mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
    getLocationPermission();
  }

  protected void getLocationPermission() {
    requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
      if (isGranted) {
        Timber.i("Location access granted");
        initializeMap();
      } else {
        Timber.w("Location access not granted");
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
          showAlert();
          initializeMap();
        }
      }
    });

    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      initializeMap();
    } else {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
  }

  /**
   * Creates a map. After this, onMapReady will be called automatically
   */
  protected void initializeMap() {
    if (mapFragment == null) {
      mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
    }
    if (mapFragment != null) mapFragment.getMapAsync(this);
  }

  /**
   * ----- Alert: Revoke permission -----
   * Enabling location access to Route42 will
   * allow you to see your location relative
   * to locations tagged by posts.
   * - OK <
   * - Cancel
   */
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
                snackbar.setAction("EXIT", view -> {
                  ((FragmentActivity) requireContext()).getSupportFragmentManager()
                          .beginTransaction()
                          .replace(R.id.fragment_container_view, new ProfileFragment())
                          .commit();
                });
              } else {
                snackbar.setAction("REVOKE", view -> getLocationPermission());
              }
              snackbar.show();
              dialog.dismiss();
            }).create().show();
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
    this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), MAP_STYLE));

    // get device location and render map
    try {
      Timber.i("getDeviceLocation: getting the devices current location");
      fusedLocationProviderClient.getLastLocation()
              .addOnSuccessListener(location -> {
                if (location != null) renderMap(location);
              });
    } catch (SecurityException e) {
      Timber.w("Unable to get current location");
      Timber.e(e);
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    } catch (RuntimeException e) {
      Timber.w("Unable to get current location");
      Timber.e(e);
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().findViewById(R.id.Btn_Create_Activity).setVisibility(View.VISIBLE);
    if (mapFragment != null) mapFragment.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mapFragment != null) mapFragment.onResume();
    else
      mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (mapFragment != null) mapFragment.onDestroyView();
  }
}