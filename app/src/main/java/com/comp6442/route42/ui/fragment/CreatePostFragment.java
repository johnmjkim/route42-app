package com.comp6442.route42.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp6442.route42.R;
import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.Point;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.viewmodel.ActiveMapViewModel;
import com.comp6442.route42.ui.viewmodel.LiveUserViewModel;
import com.comp6442.route42.utils.tasks.scheduled_tasks.PostScheduler;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePostFragment extends Fragment {

  private EditText postDescriptionInput;
  private String uid;
  private LiveUserViewModel liveUserVM;
  private ActiveMapViewModel activeMapViewModel;
  private PostRepository postRepository;
  private SwitchMaterial scheduleSwitchButton;
  private MaterialButton createPostButton;
  private int scheduledDelay = 0;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    assert getArguments() != null;
    uid = getArguments().getString("uid");
    requireActivity().findViewById(R.id.Btn_Create_Activity).setVisibility(View.INVISIBLE);

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.create_post_fragment, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    activeMapViewModel = new ViewModelProvider(requireActivity()).get(ActiveMapViewModel.class);
    liveUserVM = new ViewModelProvider(requireActivity()).get(LiveUserViewModel.class);
    scheduleSwitchButton = requireView().findViewById(R.id.create_post_schedule_switch);
    createPostButton = view.findViewById(R.id.create_post_button);
    postRepository = PostRepository.getInstance();
    ImageView postImage = view.findViewById(R.id.create_post_image);

    assert getArguments() != null;
    Bitmap myBitmap = BitmapFactory.decodeFile(requireContext().getFilesDir().getPath() + "/" + getArguments().getString("local_filename"));
    postImage.setImageBitmap(myBitmap);
    Activity userActivity = activeMapViewModel.getActivityData();
    postDescriptionInput = view.findViewById(R.id.post_description_input);
    postDescriptionInput.setText(userActivity.getPostString());
    setCancelButton();
    setSwitchButton();
    setPostButton();

  }

  /**
   * Sets behavior of the post scheduler switch.
   */
  private void setSwitchButton() {
    scheduleSwitchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(
                new ContextThemeWrapper(requireActivity(), R.style.AlertDialog_AppCompat)
        );
        dialogBuilder.setTitle("Select Delay (Minutes)")
                .setItems(PostScheduler.delayOptions, (dialogInterface, i) -> {
                  scheduledDelay = Integer.parseInt((String) PostScheduler.delayOptions[i]);
                  Toast.makeText(requireContext(), "Set post delay to " + scheduledDelay + " minute(s).", Toast.LENGTH_SHORT).show();
                }).create().show();
      }
    });
  }

  private void setPostButton() {
    createPostButton.setOnClickListener(event -> {
      createActivityPost();
      // navigate to feed
      Bundle bundle = new Bundle();
      bundle.putString("uid", uid);
      Fragment fragment = new ProfileFragment();
      fragment.setArguments(bundle);
      getActivity()
              .getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container_view, fragment)
              .commit();
    });
  }

  private void setCancelButton() {
    MaterialButton cancelPostButton = this.requireView().findViewById(R.id.cancel_post_button);
    cancelPostButton.setOnClickListener(event -> {
      Bundle bundle = new Bundle();
      bundle.putString("uid", uid);
      Fragment fragment = new FeedFragment();
      fragment.setArguments(bundle);
      activeMapViewModel.reset();
      getActivity()
              .getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container_view, fragment)
              .commit();
    });
  }

  /**
   * Creates new Post given map snapshot and the activity data collected.
   */
  private void createActivityPost() {
    User liveUser = liveUserVM.getUser().getValue();
    assert liveUser != null;
    String postDescription = postDescriptionInput.getText().toString().trim();
    Location location = activeMapViewModel.getDeviceLocation().getValue();
    Double latitude = location == null? 0.0 : location.getLatitude();
    Double longitude = location == null? 0.0 : location.getLongitude();
    List<LatLng> pastLocations = activeMapViewModel.getPastLocations();
    List<Point> route = new ArrayList<>(pastLocations.size());
    pastLocations.forEach(loc-> {
      route.add(Point.fromLatLng(loc));
    });
    assert getArguments() != null;
    String snapshotPath = getContext().getFilesDir().getPath() + "/" + getArguments().getString("local_filename");
    if (scheduleSwitchButton.isChecked()) {
      new PostScheduler(snapshotPath, activeMapViewModel.getSnapshotFileName(),
              uid,
              liveUser.getUserName(),
              liveUser.getIsPublic(),
              liveUser.getProfilePicUrl(),
              postDescription,
              route,
              "",
              latitude,
              longitude
      )
              .schedule(requireContext(), scheduledDelay);
    } else {
      Post newPost = new Post(UserRepository.getInstance().getOne(uid),
              liveUser.getUserName(),
              liveUser.getIsPublic(),
              liveUser.getProfilePicUrl(),
              new Date(),
              postDescription,
              route,
              "",
              latitude,
              longitude,
              Post.getHashTagsFromTextInput(postDescription),
              0,
              "snapshots/" + activeMapViewModel.getSnapshotFileName(),
              new ArrayList<>(0));
      savePost(newPost);
    }
    activeMapViewModel.reset();
  }


  private void savePost(Post newPost) {
    String pathToFile = getContext().getFilesDir().getPath() + "/" + getArguments().getString("local_filename");
    String storedFileName = getArguments().getString("storage_filename");
    FirebaseStorageRepository.getInstance()
            .uploadSnapshotFromLocal(pathToFile, storedFileName);
    postRepository.createOne(newPost);
  }

}