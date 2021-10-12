//package com.comp6442.route42.ui.fragment;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.BounceInterpolator;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.comp6442.route42.R;
//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.camera.CameraPosition;
//import com.mapbox.mapboxsdk.location.LocationComponent;
//import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
//import com.mapbox.mapboxsdk.location.LocationComponentOptions;
//import com.mapbox.mapboxsdk.maps.MapView;
//import com.mapbox.mapboxsdk.maps.Style;
//
//public class ActiveMapFragment extends Fragment {
//    private ActiveMapViewModel mViewModel;
//
//    public static ActiveMapFragment newInstance() {
//        return new ActiveMapFragment();
//    }
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        mapbox = Mapbox.getInstance(requireContext(), getString(R.string.mapbox_api_key));
//        // request access to fine location
//
//        String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
//        int hasLocationPermission = ContextCompat.checkSelfPermission(this.getContext(),fineLocationPermission);
//        if (!(hasLocationPermission == PackageManager.PERMISSION_GRANTED)) {
//            ActivityResultLauncher<String> requestPermissionLauncher= registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    System.out.println("permission granted");
//                } else {
//                    //navigate away from map
//                    System.out.println("permission not granted");
//                }
//            });
//            requestPermissionLauncher.launch(fineLocationPermission);
//        }
//        mViewModel = new ViewModelProvider(this).get(ActiveMapViewModel.class);
//        mViewModel.setFusedLocationProviderClient(this.getContext());
//
//        return inflater.inflate(R.layout.active_map_fragment, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mMapView = view.findViewById(R.id.mapView);
//        mMapView.onCreate(savedInstanceState);
//        Fragment self = this;
//        mMapView.getMapAsync(mapboxMap -> {
//            mapboxMap.setStyle(Style.DARK,
//                style -> {
//                    mapboxMap.getUiSettings().setZoomGesturesEnabled(false);
//
//
//                });
//            LocationComponentOptions locationComponentOptions =
//            LocationComponentOptions.builder(self.getContext())
//            .pulseEnabled(true)
//            .pulseColor(Color.GREEN)
//            .pulseAlpha(.4f)
//            .pulseInterpolator(new BounceInterpolator())
//            .build();
//
//            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
//                    .builder(self.getContext(), mapboxMap.getStyle())
//                    .locationComponentOptions(locationComponentOptions)
//                    .build();
//            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//            locationComponent.activateLocationComponent(locationComponentActivationOptions);
//
//        });
//
//    }
//
//
//}