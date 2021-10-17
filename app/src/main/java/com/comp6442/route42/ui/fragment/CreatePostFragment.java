package com.comp6442.route42.ui.fragment;

import static com.comp6442.route42.data.model.Post.getHashTagsFromTextInput;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp6442.route42.R;
import com.comp6442.route42.data.model.Activity;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.SchedulablePost;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.viewmodel.ActiveMapViewModel;
import com.comp6442.route42.ui.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

public class CreatePostFragment extends Fragment {

  private EditText postDescriptionInput;
  private String uid;
  private UserViewModel userViewModel;
  private ActiveMapViewModel activeMapViewModel;
  private PostRepository postRepository;
  private SwitchMaterial scheduleSwitchButton;
  private MaterialButton createPostButton ;

  public static CreatePostFragment newInstance() {
    return new CreatePostFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uid = getArguments().getString("uid");
    getActivity().findViewById(R.id.Btn_Create_Activity).setVisibility(View.INVISIBLE);

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
    userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    userViewModel.loadProfileUser(this.uid);
    scheduleSwitchButton = requireView().findViewById(R.id.create_post_schedule_switch);
     createPostButton = view.findViewById(R.id.create_post_button);
    postRepository = PostRepository.getInstance();
    ImageView postImage = view.findViewById(R.id.create_post_image);

    Bitmap myBitmap = BitmapFactory.decodeFile(getContext().getFilesDir().getPath() + "/" + getArguments().getString("local_filename"));
    postImage.setImageBitmap(myBitmap);
    Activity userActivity = activeMapViewModel.getActivityData();
    postDescriptionInput = view.findViewById(R.id.post_description_input);
    postDescriptionInput.setText(userActivity.getPostString());
    setCancelButton();
    setSwitchButton();
    setPostButton();

  }
  private void setSwitchButton() {
    scheduleSwitchButton.setOnCheckedChangeListener((buttonView,isChecked) -> {
      if(isChecked) {
          Timber.i("scheduled post");
          MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(
                  new ContextThemeWrapper(requireActivity(), R.style.AlertDialog_AppCompat)
          );
          dialogBuilder.setTitle("Select Delay (Minutes)")
                  .setItems(SchedulablePost.delayOptions, (dialogInterface, i) -> {
                    createActivityPost();
                    int scheduleDelay = Integer.parseInt((String) SchedulablePost.delayOptions[i]);
                    Snackbar snackbar = Snackbar.make(
                            requireView(),
                            "Posting in " + scheduleDelay + "minutes.",
                            Snackbar.LENGTH_INDEFINITE
                    );
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uid);
                    bundle.putInt("delay", scheduleDelay);
                    Fragment fragment = new FeedFragment();
                    fragment.setArguments(bundle);
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .commit();
                    snackbar.show();
                  }).create().show();
      }
    });
  }

  private void setPostButton() {
    MaterialButton createPostButton = this.requireView().findViewById(R.id.create_post_button);
      createPostButton.setOnClickListener(event -> {
        createActivityPost();
        // navigate to feed
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        Fragment fragment = new FeedFragment();
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
    User liveUser = userViewModel.getLiveUser().getValue();
    assert liveUser != null;
    String postDescription = postDescriptionInput.getText().toString().trim();
    Double latitude = activeMapViewModel.getDeviceLocation().getValue().getLatitude();
    Double longitude = activeMapViewModel.getDeviceLocation().getValue().getLongitude();
    String snapshotPath = getContext().getFilesDir().getPath() + "/" + getArguments().getString("local_filename");
    if(scheduleSwitchButton.isChecked()) {
      new SchedulablePost(snapshotPath, activeMapViewModel.getSnapshotFileName(),
                uid,
                liveUser.getUserName(),
                liveUser.getIsPublic(),
                liveUser.getProfilePicUrl(),
                postDescription,
                "",
              latitude,
              longitude
              )
              .schedule(requireContext(), 1);
    } else {
      Post newPost = new Post( UserRepository.getInstance().getOne(uid),
              liveUser.getUserName(),
              liveUser.getIsPublic(),
              liveUser.getProfilePicUrl(),
              new Date(),
              postDescription,
              "",
              latitude,
              longitude,
              getHashTagsFromTextInput(postDescription),
              0,
              "snapshots/" + activeMapViewModel.getSnapshotFileName(),
              new ArrayList<>(0));
      savePost(newPost);
    }
    activeMapViewModel.reset();
  }


  private void savePost(Post newPost)  {
    String pathToFile = getContext().getFilesDir().getPath() + "/" + getArguments().getString("local_filename");
    String storedFileName = getArguments().getString("storage_filename");
    FirebaseStorageRepository.getInstance()
            .uploadSnapshotFromLocal(pathToFile,storedFileName );
    postRepository.createOne(newPost);
  }

}