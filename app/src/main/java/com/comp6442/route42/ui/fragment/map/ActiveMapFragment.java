package com.comp6442.route42.ui.fragment.map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.R;
import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.BaseActivity;
import com.comp6442.route42.ui.fragment.CreatePostFragment;
import com.comp6442.route42.ui.viewmodel.ActiveMapViewModel;
import com.comp6442.route42.utils.MockLocation;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import timber.log.Timber;


public class ActiveMapFragment extends MapFragment {
  private final boolean demoMode;
  private boolean requestingLocationUpdates = false;
  private ActiveMapViewModel activeMapViewModel;
  private TextView activityMetricsText;
  private LocationCallback locationCallBack;

  public ActiveMapFragment() {
    super(R.id.map_fragment2, R.raw.style_json_activity_map);
    this.demoMode = BuildConfig.DEMO;
  }
  @Override
  public void onCreate(Bundle savedStateInstance) {
    super.onCreate(savedStateInstance);
    activeMapViewModel = new ViewModelProvider(requireActivity()).get(ActiveMapViewModel.class);
    activeMapViewModel.setActivityType(Activity.Activity_Type.valueOf(getArguments().getInt("activity")));
    setGetLocationCallBack();
  }
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.active_map_fragment, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (demoMode) {
      @SuppressLint("MissingPermission") Observer<Location> deviceLocationObserver = updatedLocation -> {
         //update the mock location in the provider
                    Timber.i("updated mock location to %s", updatedLocation.toString());
                     fusedLocationProviderClient.setMockLocation(updatedLocation);
      };
      activeMapViewModel.setMockDeviceLocation();
      activeMapViewModel.getDeviceLocation().observe(getViewLifecycleOwner(), deviceLocationObserver);
    }

    //set active icon
    ImageView activityIconView = view.findViewById(R.id.activity_icon);
    Glide.with(activityIconView.getContext())
            .load(Activity.Activity_Type.getIconResource(getArguments().getInt("activity")))
            .into(activityIconView);

    //set metrics
    activityMetricsText = view.findViewById(R.id.activity_metrics_text);
    FloatingActionButton activityButton = view.findViewById(R.id.activity_button);
    activityButton.setOnClickListener(click -> createActivityBtnClickHandler());
  }

  private void endUserActivity() {
    GoogleMap.SnapshotReadyCallback snapshotCallback = bitmap -> {
      try {
        String baseFilename = "activity_route";
        String localFilename = baseFilename + ".png";
        String storageFilename = baseFilename + new Date().toString() + ".png";
        Activity userActivityData = new BaseActivity(
                activeMapViewModel.getPastLocations(),
                activeMapViewModel.getElapsedTime(),
                activeMapViewModel.getActivityType()
        );
        activeMapViewModel.setActivityData(userActivityData);
        activeMapViewModel.setSnapshotFileName(storageFilename);
        // save map snapshot to local
        FileOutputStream out = requireContext().openFileOutput(localFilename, 0);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

        Bundle bundle = new Bundle();
        bundle.putString("uid", getArguments().getString("uid"));
        bundle.putString("local_filename",  localFilename);
        bundle.putString("storage_filename",  storageFilename);
        Fragment fragment = new CreatePostFragment();
        fragment.setArguments(bundle);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };

    renderSnapshotMap(activeMapViewModel.getDeviceLocation().getValue());
    googleMap.setOnMapLoadedCallback(() -> {
      googleMap.snapshot(snapshotCallback);
      googleMap.setOnMapLoadedCallback(null);
    });
  }

  @Override
  protected void initializeMap() {
    mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment2);
    assert mapFragment != null;
    mapFragment.getMapAsync(this);
  }

  private void renderSnapshotMap(Location location) {
    // Set the map's camera position to the current location of the device.
    int MAP_ZOOM_LEVEL = 18;
    int LINE_WIDTH = 25;
    int ROUTE_COLOR = Color.BLUE;
    int PADDING = 300;
    if (location != null) {
      LatLng locationLatLng = MockLocation.latLngFromLocation(location);
      // add current location marker
      googleMap.clear();
      googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(locationLatLng).title("User"));
      // add activity route polyline
      if (activeMapViewModel.hasPastLocations()) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getMapBounds(activeMapViewModel.getPastLocations()), PADDING));
        googleMap.addPolyline(
                new PolylineOptions()
                        .endCap(new RoundCap())
                        .clickable(false)
                        .addAll(activeMapViewModel.getPastLocations())
                        .width(LINE_WIDTH)
                        .color(ROUTE_COLOR)
        );
      } else {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                locationLatLng, MAP_ZOOM_LEVEL));
      }
    }
  }
  private LatLngBounds getMapBounds(List<LatLng> routePoints) {
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    for (int i=0; i< routePoints.size(); i++) {
      builder.include(routePoints.get(i));
    }
    return builder.build();
  }
  @Override
  protected void renderMap(Location location) {
    Timber.i("Received location from fusedLocationProvider: %s", location);

    // Set the map's camera position to the current location of the device.
    if (location != null) {
      LatLng locationLatLng = MockLocation.latLngFromLocation(location);

      // add current location marker
      googleMap.clear();
      googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(locationLatLng).title("User"));

      // track user using camera
      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
              locationLatLng, 20));
    }
    // update activity view model
    if (activeMapViewModel.getLastUpdateTime() != null) {
      activeMapViewModel.updateElapsedTime();
    }
    // add activity route polyline
    if (activeMapViewModel.hasPastLocations()) {
      activeMapViewModel.setLastUpdateTime(new Date());
      googleMap.addPolyline(
              new PolylineOptions()
                      .endCap(new RoundCap())
                      .clickable(false)
                      .addAll(activeMapViewModel.getPastLocations())
                      .width(25)
                      .color(Color.BLUE)
      );
    }



    Activity userActivityData = new BaseActivity(
            activeMapViewModel.getPastLocations(),
            activeMapViewModel.getElapsedTime(),
            activeMapViewModel.getActivityType()
    );
    activityMetricsText.setText(userActivityData.toString());
  }

  private void createActivityBtnClickHandler() {
    String[] dialogItems;
    if (requestingLocationUpdates)  {
      dialogItems = new String[]{"Pause", "End Activity"};
      MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(
              new ContextThemeWrapper(requireActivity(), R.style.AlertDialog_AppCompat)
      );

      dialogBuilder.setTitle("Select Action")
              .setItems(dialogItems, (dialogInterface, i) -> {
                if (i == 1) endUserActivity();
                else if (i == 0) {
                   stopLocationUpdates();
                }
              }).create().show();
    } else {
      //simply start requesting updates
      startLocationUpdates();
    }
  }

  private void stopLocationUpdates() {
    fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    requestingLocationUpdates = false;

    activeMapViewModel.setLastUpdateTime(null);
  }

  private void startLocationUpdates() {
    try {
      Timber.i("getDeviceLocation: getting the devices current location");
      requestingLocationUpdates = true;
      // request location every 1000 millis from the provider
      LocationRequest locationRequest = LocationRequest.create();
      long intervalMillis = 1000;
      locationRequest.setInterval(intervalMillis).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      fusedLocationProviderClient.setMockMode(demoMode);
      fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
    } catch (SecurityException e) {
      Timber.w("Unable to get current location");
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    } catch (RuntimeException e) {
      Timber.e(e);
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Sets a reference for the location callback required by fused location provider class.
   */
  private void setGetLocationCallBack() {
    locationCallBack = new LocationCallback() {
      @SuppressLint("MissingPermission")
      @Override
      public void onLocationResult(@NonNull LocationResult locationResult) {
        super.onLocationResult(locationResult);
        // if demo, update the mock device location
        if (demoMode) {
          activeMapViewModel.setMockDeviceLocation();
          fusedLocationProviderClient.setMockLocation(activeMapViewModel.getDeviceLocation().getValue());
        }
        else
          activeMapViewModel.setDeviceLocation(locationResult.getLastLocation());
        renderMap(locationResult.getLastLocation());
        Timber.i("location result ready");
      }

      @Override
      public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
        Timber.i("location available");
      }
    };
  }
  @Override
  public void onResume() {
    super.onResume();
    if (requestingLocationUpdates) {
      startLocationUpdates();
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    stopLocationUpdates();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    stopLocationUpdates();
  }
}