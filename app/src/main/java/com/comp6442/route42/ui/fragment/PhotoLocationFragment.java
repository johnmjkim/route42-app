package com.comp6442.route42.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.comp6442.route42.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.util.List;

import timber.log.Timber;

public class PhotoLocationFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener {
  private static final String ARG_PARAM1 = "lat";
  private static final String ARG_PARAM2 = "lon";
  private Double lat, lon;
  private LatLng userLocation, imageLocation;

  private Mapbox mapbox;
  private MapboxMap mapboxMap;
  private MapView mapView;
  private CircleManager circleManager;
  private PermissionsManager permissionsManager;
  private SymbolManager symbolManager;

  public PhotoLocationFragment() {
    // Required empty public constructor
  }

  public static ProfileFragment newInstance(Double param1, Double param2) {
    Timber.i("New instance created with param (%f, %f)", param1, param2);
    ProfileFragment fragment = new ProfileFragment();
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

  @SuppressLint("MissingPermission")
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    mapbox = Mapbox.getInstance(requireContext(), getString(R.string.mapbox_api_key));
    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    userLocation = new LatLng(-33.872789d, 151.205554d);

    if (savedInstanceState != null) {
      //Restore the fragment state
      this.lat = savedInstanceState.getDouble(ARG_PARAM1);
      this.lon = savedInstanceState.getDouble(ARG_PARAM2);
      Timber.i("Restoring fragment state (%f, %f)", lat, lon);
    }

    imageLocation = new LatLng(lat, lon);

    Timber.i("Creating map. User location: (lat, lon) = %f, %f",
            imageLocation.getLatitude(),
            imageLocation.getLongitude());

    mapView = view.findViewById(R.id.fragment_map);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(mapboxMap ->
            mapboxMap.setStyle(
                    Style.DARK, style -> initMapStyle(mapboxMap, style)
            )
    );
  }

  @SuppressLint("DefaultLocale")
  private void initMapStyle(MapboxMap mapboxMap, Style style) {
    this.mapboxMap = mapboxMap;

    mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
    enableLocationComponent(style);

    if (style.isFullyLoaded()) {
      CircleOptions imageLocCircle = new CircleOptions()
              .withLatLng(imageLocation)
              .withCircleColor(ColorUtils.colorToRgbaString(Color.rgb(255, 50, 50)))
              .withCircleRadius(20f)
              .withDraggable(false);

      circleManager = new CircleManager(mapView, mapboxMap, style);
      circleManager.create(imageLocCircle);
      circleManager.addClickListener(point -> {
        Toast.makeText(getActivity(),
                String.format("clicked %s", point.getId()),
                Toast.LENGTH_SHORT
        ).show();
        return false;
      });

//      Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin);
//      mapboxMap.getStyle().addImage("map-marker", bmp);

//      symbolManager = new SymbolManager(mapView, mapboxMap, style);
//      symbolManager.setIconAllowOverlap(true);
//      symbolManager.setTextAllowOverlap(false);
//      symbolManager.addClickListener(symbol -> {
//        Toast.makeText(requireActivity(), "clicked  " + symbol.getTextField().toLowerCase(), Toast.LENGTH_SHORT).show();
//        return false;
//      });

//      symbolManager.create(new SymbolOptions()
//              .withLatLng(imageLocation)
//              .withIconImage("map-marker")
//              .withIconSize(50f)
//              .withIconOffset(new Float[]{0f, -1.5f})
//              .withTextHaloColor("rgba(255, 255, 255, 100)")
//              .withTextHaloWidth(5.0f)
//              .withTextAnchor("top")
//              .withTextOffset(new Float[]{0f, 1.5f})
//              .withTextField(String.format("(%f, %f)", lat, lon))
//      );

      CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(imageLocation)
              .zoom(calculateDistance(userLocation, imageLocation))
              .build();

      mapboxMap.setCameraPosition(cameraPosition);
    }
  }

  @SuppressWarnings({"MissingPermission"})
  private void enableLocationComponent(@NonNull Style loadedMapStyle) {
    if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {
      LocationComponent locationComponent = mapboxMap.getLocationComponent();
      locationComponent.activateLocationComponent(requireActivity(), loadedMapStyle);
      locationComponent.setLocationComponentEnabled(true);
      locationComponent.setCameraMode(CameraMode.TRACKING);
      locationComponent.setRenderMode(RenderMode.COMPASS);
    } else {
      permissionsManager = new PermissionsManager(new PermissionsListener() {
        @Override
        public void onExplanationNeeded(List<String> permissionsToExplain) {
          Toast.makeText(getActivity(), "location not enabled", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPermissionResult(boolean granted) {
          if (granted) {
            mapboxMap.getStyle(style -> initMapStyle(mapboxMap, style));
          } else {
            Toast.makeText(getActivity(), "Location services not allowed", Toast.LENGTH_LONG).show();
          }
        }
      });
      permissionsManager.requestLocationPermissions(requireActivity());
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private int calculateDistance(LatLng source, LatLng destination) {
    double distance = source.distanceTo(destination);

    if (distance / 1000 < 1) {
      return 14;
    } else if (distance / 1000 < 5) {
      return 12;
    } else if (distance / 1000 < 10) {
      return 11;
    } else if (distance / 1000 < 30) {
      return 10;
    } else if (distance / 1000 < 50) {
      return 9;
    } else if (distance / 1000 < 100) {
      return 8;
    } else if (distance / 1000 < 150) {
      return 7;
    } else if (distance / 1000 < 450) {
      return 6;
    } else if (distance / 1000 < 900) {
      return 5;
    } else {
      return 4;
    }
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mapView != null) mapView.onSaveInstanceState(outState);
    Timber.d("Saving instance state");
  }

  @Override
  public void onStart() {
    super.onStart();
    if (mapView != null) mapView.onStart();
    Timber.d("Starting");
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mapView != null) mapView.onStop();
    Timber.d("Stopping");
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mapView != null) mapView.onResume();
    Timber.d("Resuming");
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mapView != null) mapView.onPause();
    Timber.d("Pausing");
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (circleManager != null) circleManager.onDestroy();
    if (mapView != null) mapView.onDestroy();
    Timber.d("Destroying View");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mapView = null;
    Timber.d("Detaching");
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    Timber.d("OptionMenu Created");
  }

  @Override
  public boolean onMapClick(@NonNull LatLng point) {
    return false;
  }

  @Override
  public void onMapReady(@NonNull MapboxMap mapboxMap) {

  }
}