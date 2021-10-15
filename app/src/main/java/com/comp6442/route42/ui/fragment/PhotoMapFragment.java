package com.comp6442.route42.ui.fragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.comp6442.route42.R;
import com.comp6442.route42.api.KNearestNeighbourService;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.PostRepository;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

public class PhotoMapFragment extends MapFragment {
  private static final String ARG_PARAM1 = "posts";
  private static final String ARG_PARAM2 = "drawLine";
  private static final float ZOOM = 12f;
  private static final boolean useKDTree = true;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private List<Post> posts = new ArrayList<>();

  public PhotoMapFragment() {
    super(R.id.map_fragment, R.raw.style_json);
  }

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
    super.setHasOptionsMenu(true);

    if (getArguments() != null) {
      this.posts = getArguments().getParcelableArrayList(ARG_PARAM1);
    }
    if (this.posts == null) {
      this.posts = new ArrayList<>();
    }

    Timber.i("Creating map with %d posts", posts.size());
    Timber.d(posts.toString());
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_photo_map, container, false);
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
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getLocationPermission();
  }

  /**
   * posts.size() == 0 && userLocation == null: set user location as sydney, geo search
   * posts.size() == 0 && userLocation != null: geo search based on user location (points)
   * posts.size() >= 1: render posts as points
   * TODO when user taps on "only once" or "deny" and then approve, map should update with user's location
   */
  protected void renderMap(Location location) {
    Timber.i("Rendering map");

    final int FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION);
    LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

    googleMap.setMyLocationEnabled(FINE_LOCATION_PERMISSION == PERMISSION_GRANTED);
    googleMap.getUiSettings().setMyLocationButtonEnabled(FINE_LOCATION_PERMISSION == PERMISSION_GRANTED);

    googleMap.moveCamera(CameraUpdateFactory.newLatLng(center));

    if (posts != null && posts.size() > 0) {
      renderPosts(googleMap, posts);
    } else {
      if (useKDTree) {
        posts = getKNearestNeighbor(50, center);
        renderPosts(googleMap, posts);
      } else {
        geoQuery(center);
      }
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
    Timber.i("Beginning KNN geoQuery with K: %d location: %s", k, location.toString());
    KNearestNeighbourService knn = new KNearestNeighbourService(k, location.latitude, location.longitude);
    Future<List<Post>> future = executor.submit(knn);

    try {
      return future.get();
    } catch (InterruptedException | ExecutionException | JsonSyntaxException e) {
      Timber.e(e);
      return new ArrayList<>();
    }
  }

  private void geoQuery(LatLng location) {
    Timber.i("Beginning geoQuery with userLocation: %s", location.toString());

    final double radiusInM = 50 * 1000;
    final List<Task<QuerySnapshot>> tasks = PostRepository.getInstance().getPostsWithinRadius(location, radiusInM, 50);
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
                  GeoLocation center = new GeoLocation(location.latitude, location.longitude);
                  double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                  if (distanceInM <= radiusInM) posts.add(doc.toObject(Post.class));
                }
              }

              Timber.i("Found %d documents within %f km", posts.size(), radiusInM);
              if (matchingDocs.size() > 0) renderPosts(googleMap, posts);
            });
  }

  private void renderPosts(GoogleMap googleMap, List<Post> posts) {
    if (posts != null && posts.size() > 0) {
      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      for (Post post : posts) {
        LatLng point = new LatLng(post.getLatitude(), post.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(point).title(post.getLocationName()));
        builder.include(point);
      }

      LatLngBounds bounds = builder.build();
      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 300);
      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), ZOOM));
      Handler handler = new Handler();
      handler.postDelayed(() -> googleMap.animateCamera(cameraUpdate), 1000);
    }
  }
}