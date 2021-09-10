package com.comp6442.groupproject.ui.fragment;

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

import com.comp6442.groupproject.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class MapFragment extends Fragment {
  private static final String TAG = "MapFragment";
  private final Random random = new Random();

  private Mapbox mapbox;
  private MapView mMapView;
  private LineManager lineManager;

  public MapFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    mapbox = Mapbox.getInstance(requireContext(), getString(R.string.mapbox_api_key));
    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Timber.d("Creating map..");
    mMapView = view.findViewById(R.id.mapView);
    mMapView.onCreate(savedInstanceState);
    mMapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.DARK,
            style -> {
              mapboxMap.getUiSettings().setZoomGesturesEnabled(true);

              if (style.isFullyLoaded()) {
                lineManager = new LineManager(mMapView, mapboxMap, style);
                lineManager.addClickListener(line -> {
                  Toast.makeText(getActivity(),
                          String.format("Line clicked %s", line.getId()),
                          Toast.LENGTH_SHORT
                  ).show();
                  return false;
                });
                lineManager.addLongClickListener(line -> {
                  Toast.makeText(getActivity(),
                          String.format("Line long clicked %s", line.getId()),
                          Toast.LENGTH_SHORT
                  ).show();
                  return false;
                });

                // create a fixed line
                List<LatLng> route = Arrays.asList(
                        new LatLng(-33.884633, 151.194464),
                        new LatLng(-33.884889, 151.194453),
                        new LatLng(-33.884986, 151.194464),
                        new LatLng(-33.885154, 151.194378),
                        new LatLng(-33.885371, 151.194288),
                        new LatLng(-33.885517, 151.194197),
                        new LatLng(-33.885645, 151.194150),
                        new LatLng(-33.885826, 151.194059),
                        new LatLng(-33.885981, 151.194022),
                        new LatLng(-33.886171, 151.193905),
                        new LatLng(-33.886343, 151.193809),
                        new LatLng(-33.886533, 151.193761),
                        new LatLng(-33.886666, 151.193702),
                        new LatLng(-33.886644, 151.193657),
                        new LatLng(-33.887127, 151.193300),
                        new LatLng(-33.887265, 151.193205),
                        new LatLng(-33.887477, 151.192983),
                        new LatLng(-33.887722, 151.192733),
                        new LatLng(-33.887639, 151.192455),
                        new LatLng(-33.887542, 151.192149),
                        new LatLng(-33.887454, 151.191854),
                        new LatLng(-33.887357, 151.191549),
                        new LatLng(-33.887283, 151.191243),
                        new LatLng(-33.886914, 151.191165),
                        new LatLng(-33.886743, 151.191159),
                        new LatLng(-33.886517, 151.191182),
                        new LatLng(-33.886337, 151.191221),
                        new LatLng(-33.886116, 151.191371),
                        new LatLng(-33.885844, 151.191393),
                        new LatLng(-33.885488, 151.191499),
                        new LatLng(-33.885220, 151.191543),
                        new LatLng(-33.884902, 151.191927),
                        new LatLng(-33.884851, 151.192433),
                        new LatLng(-33.884814, 151.192944),
                        new LatLng(-33.884741, 151.193389),
                        new LatLng(-33.884741, 151.194017)
                );

                LineOptions lineOptions = new LineOptions()
                        .withLatLngs(route)
                        .withLineColor(ColorUtils.colorToRgbaString(Color.rgb(0, 255, 204)))
                        .withLineWidth(5.0f);
                lineManager.create(lineOptions);

                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(route.get(0))
                        .zoom(calculateDistance(route.get(0), route.get(route.size() - 1)))
                        .build());
              }
            }));
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

  private List<LatLng> createRandomLatLngs() {
    List<LatLng> latLngs = new ArrayList<>();
    for (int i = 0; i < random.nextInt(10); i++) {
      latLngs.add(new LatLng((random.nextDouble() * -180.0) + 90.0,
              (random.nextDouble() * -360.0) + 180.0));
    }
    return latLngs;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    mMapView.onSaveInstanceState(outState);
    Timber.d("Saving instance state");
  }

  @Override
  public void onStart() {
    super.onStart();
    mMapView.onStart();
    Timber.d("Starting");
  }

  @Override
  public void onStop() {
    super.onStop();
    mMapView.onStop();
    Timber.d("Stopping");
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.onResume();
    Timber.d("Resuming");
  }

  @Override
  public void onPause() {
    super.onPause();
    mMapView.onPause();
    Timber.d("Pausing");
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (lineManager != null) {
      lineManager.onDestroy();
    }
    mMapView.onDestroy();
    Timber.d("Destroying View");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mMapView = null;
    Timber.d("Detaching");
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    Timber.d("OptionMenu Created");
  }
}