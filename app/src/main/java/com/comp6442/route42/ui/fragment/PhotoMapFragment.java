package com.comp6442.route42.ui.fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

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
import com.comp6442.route42.api.KNearestNeighbourService;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.PostRepository;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

public class PhotoMapFragment extends Fragment implements LocationListener, OnMapReadyCallback {
  private static final String ARG_PARAM1 = "posts";
  private static final String ARG_PARAM2 = "drawLine";
  private List<Post> posts = new ArrayList<>();
  private Location currentLocation = null;
  private LatLng userLocation;
  private SupportMapFragment mapFragment;
  private GoogleMap googleMap;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private ActivityResultLauncher<String> requestPermissionLauncher;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private static final boolean useKDTree = true;
  private Marker currentLocationMarker;
  private static final Float ZOOM = 12f;

  public static PhotoMapFragment newInstance(List<Post> param1, boolean param2) {
    Timber.i("%d posts received, drawLine = %s", param1.size(), param2);
    PhotoMapFragment fragment = new PhotoMapFragment();

    Bundle args = new Bundle();
    args.putParcelableArrayList(ARG_PARAM1, (ArrayList<Post>) param1);
    args.putBoolean(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    // get location provider client
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    Timber.d("Creating view");
    // reveal bottom nav if hidden
    BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    bottomNavView.animate().translationY(0).setDuration(250);

    if (getArguments() != null) {
      this.posts = getArguments().getParcelableArrayList(ARG_PARAM1);
    }

    if (this.posts == null) {
      this.posts = new ArrayList<>();
    }

    Timber.i("Received %d posts", posts.size());
    getLocationPermission();
    return inflater.inflate(R.layout.fragment_photo_map, container, false);
  }

  private void initializeMap() {
    if (mapFragment == null) {
      mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
    }
    if (mapFragment != null) mapFragment.getMapAsync(this);
  }

  /**
   *    ----- Alert: Revoke permission -----
   *  Enabling location access to Route42 will
   *  allow you to see your location relative
   *  to locations tagged by posts.
   *    - OK <
   *    - Cancel
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
    requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
      if (isGranted) {
        Timber.i("Location access granted");
        initializeMap();
      } else {
        Timber.w("Location access not granted");
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
          showAlert();
        } else {
          Snackbar snackbar = Snackbar.make(mapFragment.requireView(), "Permission not granted", Snackbar.LENGTH_INDEFINITE);
          snackbar.setAction("EXIT", view -> {
            requireActivity().finishAffinity();
            System.exit(0);
          });
          snackbar.show();
        }
      }
    });

    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      initializeMap();
    } else {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
  }

  private Task<Location> getDeviceLocation() {
    try {
      Timber.i("getDeviceLocation: getting the devices current location");
      return fusedLocationProviderClient.getLastLocation();
    } catch (SecurityException e) {
      Timber.w("Unable to get current location");
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    } catch (RuntimeException e) {
      Timber.e(e);
      Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
    }
    return null;
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera.
   * If Google Play services is not installed on the device, the user will be prompted to
   * install it inside the SupportMapFragment. This method will only be triggered once the
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
                if (task.isSuccessful()) {
                  this.currentLocation = task.getResult();
                  renderMap();
                } else initializeMap();
              });
    } else {
      snackbar.show();
    }
  }

  /**
   * posts.size() == 0 && userLocation == null: set user location as sydney, geo search
   * posts.size() == 0 && userLocation != null: geo search based on user location (points)
   * posts.size() >= 1: render posts as points
   * TODO posts.size() > 1: points
   * TODO when user taps on "only once" or "deny" and then approve, map should update with user's location
   */
  private void renderMap() {
    Timber.i("Rendering map");
    final int FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION);

    assert this.posts != null;

    if (FINE_LOCATION_PERMISSION == PERMISSION_GRANTED && currentLocation == null) {
      Timber.w("Locattion permission granted, but currentLocation is null");
    }

    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json));

    if (currentLocation == null) {
      Timber.i("User location not available");
      userLocation = new LatLng(-33.8523f, 151.2108f);
//      googleMap.setMyLocationEnabled(false);
      googleMap.setMyLocationEnabled(true);
      googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    } else {
      Timber.i("User location: %s", userLocation);
      googleMap.setMyLocationEnabled(true);
      googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    googleMap.addMarker(new MarkerOptions().position(userLocation).title("User").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

    if (posts.size() == 0) {
      if (useKDTree) {
        posts = getKNearestNeighbor(50, userLocation);
        renderPosts(googleMap, posts);
      } else {
        geoQuery(userLocation);
      }
    } else {
      renderPosts(googleMap, posts);
    }
  }

  private void plotLine(LatLng userLocation, LatLng location) {
    googleMap.addMarker(new MarkerOptions().position(location).title("Image"));
    googleMap.addPolyline(new PolylineOptions().add(userLocation, location).width(5).color(Color.RED));

    LatLngBounds bounds = new LatLngBounds.Builder()
            .include(userLocation)
            .include(location)
            .build();

    int padding = 300; // offset from edges of the map in pixels
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
    Handler handler = new Handler();
    handler.postDelayed(() -> googleMap.animateCamera(cameraUpdate), 1000);
  }

  private List<Post> getKNearestNeighbor(int k, LatLng location) {
    final GeoLocation center = new GeoLocation(location.latitude, location.longitude);
    Timber.i("Beginning KNN geoQuery with K: %d userLocation: %s", k, location.toString());
    KNearestNeighbourService knnapi = new KNearestNeighbourService(k, location.latitude, location.longitude);
    Future<List<Post>> future = executor.submit(knnapi);

    try {
      return future.get();
    } catch (InterruptedException | ExecutionException | JsonSyntaxException e) {
      Timber.e(e);
      return new ArrayList<>();
    }
  }


  private void geoQuery(LatLng location) {
    Timber.i("Beginning geoQuery with userLocation: %s", location.toString());
    final GeoLocation center = new GeoLocation(location.latitude, location.longitude);
    final double radiusInM = 50 * 1000;
    final List<Task<QuerySnapshot>> tasks = PostRepository.getInstance().getPostsWithinRadius(center, radiusInM, 50);
    final List<DocumentSnapshot> matchingDocs = new ArrayList<>();

    // Collect all the query results together into a single list
    Tasks.whenAllComplete(tasks).addOnCompleteListener(
            unused -> {
              for (Task<QuerySnapshot> task : tasks) {
                QuerySnapshot snap = task.getResult();

                List<Post> posts = new ArrayList<>();
                for (DocumentSnapshot doc : snap.getDocuments()) {
                  // Filter out a few false positives due to GeoHash accuracy, but most will match
                  GeoLocation docLocation = new GeoLocation(doc.getDouble("latitude"), doc.getDouble("longitude"));
                  double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                  if (distanceInM <= radiusInM) posts.add(doc.toObject(Post.class));
                }
              }

              Timber.i("Found %d documents within %f km", posts.size(), radiusInM);
              if (matchingDocs.size() > 0) renderPosts(googleMap, posts);
            });
  }

  private void renderPosts(GoogleMap googleMap, List<Post> posts) {
    assert userLocation != null;

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    for (Post post : posts) {
      LatLng point = new LatLng(post.getLatitude(), post.getLongitude());
      googleMap.addMarker(new MarkerOptions().position(point).title(post.getLocationName()));
      builder.include(point);
    }

    LatLngBounds bounds = builder.build();
    int padding = 300; // offset from edges of the map in pixels
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM));
    Handler handler = new Handler();
    handler.postDelayed(() -> googleMap.animateCamera(cameraUpdate), 1000);
  }

  @Override
  public void onResume() {
    super.onResume();
    initializeMap();
  }

  @Override
  public void onLocationChanged(@NonNull Location location) {
    if (currentLocationMarker != null) {
      currentLocationMarker.remove();
    }

    // Update current location
    currentLocation = location;
    userLocation = new LatLng(location.getLatitude(), location.getLongitude());

    // Update map
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(userLocation);
    markerOptions.title("Current Position");
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
    currentLocationMarker = googleMap.addMarker(markerOptions);
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM));
  }


  @Override
  public void onPause() {
    super.onPause();
    if (mapFragment != null) mapFragment.onPause();
  }


//  @Override
//  public void onResume() {
//    super.onResume();
//    if (mapFragment != null) mapFragment.onResume();
//  }
//
//  @Override
//  public void onDestroyView() {
//    super.onDestroyView();
//    if (mapFragment != null) mapFragment.onDestroyView();
//  }
}