package com.comp6442.route42.ui.fragment;

import static com.comp6442.route42.data.model.Post.getHashTagsFromTextInput;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.viewmodel.ActiveMapViewModel;
import com.comp6442.route42.ui.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Date;

public class CreatePostFragment extends Fragment {

  private EditText postDescriptionInput;
  private String uid;
  private UserViewModel userViewModel;
  private ActiveMapViewModel activeMapViewModel;
  private PostRepository postRepository;

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

    postRepository = PostRepository.getInstance();
    ImageView postImage = view.findViewById(R.id.create_post_image);

    Bitmap myBitmap = BitmapFactory.decodeFile(getContext().getFilesDir().getPath() + "/" + getArguments().getString("local_filename"));
    postImage.setImageBitmap(myBitmap);
    Activity userActivity = activeMapViewModel.getActivityData();
    postDescriptionInput = view.findViewById(R.id.post_description_input);
    postDescriptionInput.setText(userActivity.getPostString());
    setCancelButton();
    setPostButton();


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

  /**
   * Creates new Post given map snapshot and the activity data collected.
   */
  private void createActivityPost() {
    User liveUser = userViewModel.getLiveUser().getValue();
    assert liveUser != null;
    String profilePicUrl = liveUser.getProfilePicUrl();
    FirebaseStorageRepository.getInstance().uploadSnapshotFromLocal(getArguments().getString("local_filename"),getArguments().getString("storage_filename"), getContext().getFilesDir().getPath());
    String postDescription = postDescriptionInput.getText().toString().trim();
    Double latitude = activeMapViewModel.getDeviceLocation().getValue().getLatitude();
    Double longitude = activeMapViewModel.getDeviceLocation().getValue().getLongitude();
    String imageUrl = "snapshots/" + activeMapViewModel.getSnapshotFileName();
    Post newPost = new Post( UserRepository.getInstance().getOne(uid),
            liveUser.getUserName(),
            liveUser.getIsPublic(),
            profilePicUrl,
            new Date(),
            postDescription,
            "",
            latitude,
            longitude,
            getHashTagsFromTextInput(postDescription),
            0,
            imageUrl,
            new ArrayList<>(0));
    postRepository.createOne(newPost);
    activeMapViewModel.reset();

  }

}