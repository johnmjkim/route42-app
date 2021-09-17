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
import com.comp6442.route42.data.repository.PostRepository;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class MapboxFragment extends Fragment {
  private static final String ARG_PARAM1 = "points";

  private Mapbox mapbox;
  private MapView mMapView;
  private CircleManager circleManager;
  private LatLng userLocation;
  private Random random = new Random();
  private List<DocumentSnapshot> matchingDocs = new ArrayList<>();

  private FusedLocationProviderClient fusedLocationClient;

  public MapboxFragment() {
    // Required empty public constructor
  }

  @SuppressLint("MissingPermission")
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    mapbox = Mapbox.getInstance(requireContext(), getString(R.string.mapbox_api_key));

    //    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//    fusedLocationClient.getLastLocation().addOnSuccessListener(
//            location -> userLocation = new LatLng(location.getLatitude(), location.getLongitude())
//    );
    userLocation = new LatLng(-32.7125008995674, 151.52910736650574);

    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (userLocation != null) {
      Timber.i("Creating map. User location: (lat, lon) = %f, %f", userLocation.getLatitude(), userLocation.getLongitude());

      final GeoLocation center = new GeoLocation(userLocation.getLatitude(), userLocation.getLongitude());
      final double radiusInM = 5000 * 1000;

      final List<Task<QuerySnapshot>> tasks = PostRepository.getInstance().getPostsWithinRadius(center, radiusInM);

      // Collect all the query results together into a single list
      Tasks.whenAllComplete(tasks)
              .addOnCompleteListener(t -> {
                for (Task<QuerySnapshot> task : tasks) {
                  QuerySnapshot snap = task.getResult();
                  for (DocumentSnapshot doc : snap.getDocuments()) {
                    double lat = doc.getDouble("latitude");
                    double lng = doc.getDouble("longitude");

                    // We have to filter out a few false positives due to GeoHash
                    // accuracy, but most will match
                    GeoLocation docLocation = new GeoLocation(lat, lng);
                    double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                    if (distanceInM <= radiusInM) {
                      matchingDocs.add(doc);
                    }
                  }
                }

                // matchingDocs contains the results
                Timber.i("Found %d documents within %f", matchingDocs.size(), radiusInM);
//                mMapView = view.findViewById(R.id.map);
                mMapView.onCreate(savedInstanceState);
                mMapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.DARK,
                        style -> {
                          mapboxMap.getUiSettings().setZoomGesturesEnabled(true);

                          if (style.isFullyLoaded()) {
                            circleManager = new CircleManager(mMapView, mapboxMap, style);

                            circleManager.addClickListener(point -> {
                              Toast.makeText(getActivity(),
                                      String.format("Line clicked %s", point.getId()),
                                      Toast.LENGTH_SHORT
                              ).show();
                              return false;
                            });

                            circleManager.addLongClickListener(point -> {
                              Toast.makeText(getActivity(),
                                      String.format("Line long clicked %s", point.getId()),
                                      Toast.LENGTH_SHORT
                              ).show();
                              return false;
                            });

                            if (matchingDocs != null && matchingDocs.size() > 0) {
                              List<LatLng> points = new ArrayList<>();
                              List<CircleOptions> circleOptionsList = new ArrayList<>();

                              for (DocumentSnapshot matchingDoc : matchingDocs) {
                                if (matchingDoc.contains("latitude") && matchingDocs.contains("longitude")){
                                  LatLng point = new LatLng(
                                          matchingDoc.getDouble("latitude"),
                                          matchingDoc.getDouble("longitude")
                                  );
                                  points.add(point);

                                  CircleOptions options = new CircleOptions()
                                          .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                                          .withCircleColor(ColorUtils.colorToRgbaString(Color.rgb(0, 255, 204)))
                                          .withCircleRadius(20f)
                                          .withDraggable(false);
                                  circleOptionsList.add(options);
                                }
                              }

                              circleManager.create(circleOptionsList);
                              mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                      .target(userLocation)
                                      .zoom(calculateDistance(points.get(0), points.get(points.size() - 1)))
                                      .build());
                            }
                          }
                        }));
              });

    }
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
    if (mMapView != null) mMapView.onSaveInstanceState(outState);
    Timber.d("Saving instance state");
  }

  @Override
  public void onStart() {
    super.onStart();
    if (mMapView != null) mMapView.onStart();
    Timber.d("Starting");
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mMapView != null) mMapView.onStop();
    Timber.d("Stopping");
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mMapView != null) mMapView.onResume();
    Timber.d("Resuming");
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mMapView != null) mMapView.onPause();
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
    if (circleManager != null) circleManager.onDestroy();
    if (mMapView != null) mMapView.onDestroy();
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