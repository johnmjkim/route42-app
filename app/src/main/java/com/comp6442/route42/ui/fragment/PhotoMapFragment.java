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
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.PostRepository;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PhotoMapFragment extends Fragment implements OnMapReadyCallback {
  private static final String ARG_PARAM1 = "posts";
  private static final String ARG_PARAM2 = "drawLine";
  private List<Post> posts = new ArrayList<>();
  private Location currentLocation = null;
  private LatLng userLocation;
  private SupportMapFragment mapFragment;
  private GoogleMap googleMap;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private boolean locationPermissionGranted = false;
  private ActivityResultLauncher<String> requestPermissionLauncher;

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

    // reveal bottom nav if hidden
    BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    bottomNavView.animate().translationY(0).setDuration(250);

    if (getArguments() != null) {
      this.posts = getArguments().getParcelableArrayList(ARG_PARAM1);
    }

    if (this.posts == null) {
      this.posts = new ArrayList<>();
    }

    Timber.i("Creating map with %d posts", posts.size());
    Timber.d(posts.toString());
  }

//  @Override
//  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//
//    return inflater.inflate(R.layout.fragment_photo_map, container, false);
//  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getLocationPermission();
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

  /**
   * posts.size() == 0 && userLocation == null: blank
   * posts.size() == 0 && userLocation != null: geo search based on user location (points)
   * posts.size() == 1 && userLocation == null: single point
   * posts.size() == 1 && userLocation != null: one polyline
   * TODO posts.size() > 1: points
   * TODO when user taps on "only once" or "deny" and then approve, map should update with user's location
   */
  private void renderMap() {
    Timber.i("Rendering map");
    final int FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
    final int GRANTED = PackageManager.PERMISSION_GRANTED;

    assert this.posts != null;

    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json));

    if (FINE_LOCATION_PERMISSION == GRANTED && currentLocation != null) {
      userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
      googleMap.addMarker(new MarkerOptions().position(userLocation).title("User"));
      googleMap.setMyLocationEnabled(true);
      googleMap.getUiSettings().setMyLocationButtonEnabled(true);
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

      if (posts.size() == 0) geoQuery();
      else if (posts.size() == 1) {
        Timber.i("1 post received. Drawing line.");
        // draw polyline
        LatLng imageLocation = posts.get(0).getLatLng();
        googleMap.addMarker(new MarkerOptions().position(imageLocation).title("Image"));
        googleMap.addPolyline(new PolylineOptions().add(userLocation, imageLocation).width(5).color(Color.RED));

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(userLocation)
                .include(imageLocation)
                .build();

        int padding = 300; // offset from edges of the map in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        Handler handler = new Handler();
        handler.postDelayed(() -> googleMap.animateCamera(cameraUpdate), 1000);
      }
    } else {
      Timber.i("User location not available, plotting a single point");
      googleMap.setMyLocationEnabled(false);
      googleMap.getUiSettings().setMyLocationButtonEnabled(false);

      if (posts.size() == 1) {
        googleMap.addMarker(new MarkerOptions().position(posts.get(0).getLatLng()).title("Image"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posts.get(0).getLatLng(), 12f));
      }
    }
  }

  private void geoQuery() {
    Timber.i("Beginning geoQuery with userLocation: %s", userLocation.toString());
    final GeoLocation center = new GeoLocation(userLocation.latitude, userLocation.longitude);
    final double radiusInM = 50 * 1000;
    final List<Task<QuerySnapshot>> tasks = PostRepository.getInstance().getPostsWithinRadius(center, radiusInM, 50);
    final List<DocumentSnapshot> matchingDocs = new ArrayList<>();

    // Collect all the query results together into a single list
    Tasks.whenAllComplete(tasks).addOnCompleteListener(
            unused -> {
              for (Task<QuerySnapshot> task : tasks) {
                QuerySnapshot snap = task.getResult();

                for (DocumentSnapshot doc : snap.getDocuments()) {
                  // Filter out a few false positives due to GeoHash accuracy, but most will match
                  GeoLocation docLocation = new GeoLocation(doc.getDouble("latitude"), doc.getDouble("longitude"));
                  double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                  if (distanceInM <= radiusInM) matchingDocs.add(doc);
                }
              }

              Timber.i("Found %d documents within %f km", matchingDocs.size(), radiusInM);

              if (matchingDocs.size() > 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (DocumentSnapshot matchingDoc : matchingDocs) {
                  if (matchingDoc.contains("latitude") && matchingDoc.contains("longitude")) {
                    Post post = matchingDoc.toObject(Post.class);
                    assert post != null;
                    LatLng point = new LatLng(post.getLatitude(), post.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(point).title(post.getLocationName()));
                    builder.include(point);
                  }
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f));
                LatLngBounds bounds = builder.build();
                int padding = 300; // offset from edges of the map in pixels
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                Handler handler = new Handler();
                handler.postDelayed(() -> googleMap.animateCamera(cameraUpdate), 1000);
              }
            });
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