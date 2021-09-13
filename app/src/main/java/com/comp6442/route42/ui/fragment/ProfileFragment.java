package com.comp6442.route42.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.UserViewModel;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.activity.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ProfileFragment extends Fragment {
  private static final String ARG_PARAM1 = "uid";
  private final List<ListenerRegistration> firebaseListenerRegs = new ArrayList<>();
  private FirebaseAuth mAuth;
  private String uid;
  private UserViewModel viewModel;
  private TextView userNameView, followerCountView, followingCountView;
  private Button blockButton, followButton;

  public ProfileFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @return A new instance of fragment HomeFragment.
   */
  public static ProfileFragment newInstance(String param1) {
    Timber.i("New instance created with param %s", param1);
    ProfileFragment fragment = new ProfileFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * The callback also receives a savedInstanceState Bundle argument containing any state
   * previously saved by onSaveInstanceState(). Note that savedInstanceState has
   * a null value the first time the fragment is created
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.mAuth = FirebaseAuthLiveData.getInstance().getAuth();

    if (getArguments() != null) {
      this.uid = getArguments().getString(ARG_PARAM1);
    }
    Timber.i("onCreate called with uid %s", uid);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Timber.d("breadcrumb");
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Timber.d("breadcrumb");

    viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    userNameView = view.findViewById(R.id.profile_username);
    blockButton = view.findViewById(R.id.profile_block_button);
    followButton = view.findViewById(R.id.profile_follow_button);
    followerCountView = view.findViewById(R.id.profile_primary_text);
    followingCountView = view.findViewById(R.id.profile_secondary_text);

    if (savedInstanceState != null) {
      //Restore the fragment's state here
      this.uid = savedInstanceState.getString("uid");
      Timber.i("Restoring fragment state for uid: %s", this.uid);
    }

    if (this.uid != null) {
      // TODO: find out where double quotes entered uid
      // Timber.i("Received uid: %s", this.uid);
      if (this.uid.contains("\"")) this.uid = this.uid.replaceAll("^\"|\"$", "");
      Timber.i("Cleaned uid: %s", this.uid);

      // create observer to update the profile UI on change to the `ProfileUser`
      final Observer<User> userObserver = profileUser -> renderProfile(profileUser, view);

      // initialize profileUser, observe change to the profileUser data, and get a registration
      viewModel.loadProfileUser(this.uid);
      viewModel.getProfileUser().observe(getViewLifecycleOwner(), userObserver);
      firebaseListenerRegs.add(viewModel.addSnapshotListenerToProfileUser(this.uid));
    } else {
      Timber.w("uid is null");
    }
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    Timber.d("breadcrumb");
    /* It is strongly recommended to tie Lifecycle-aware components to the STARTED state of a
    fragment, as this state guarantees that the fragment's view is available, if one was created,
    and that it is safe to perform a FragmentTransaction on the child FragmentManager of the fragment.
    If the fragment's view is non-null, the fragment's view Lifecycle is moved to STARTED
    immediately after the fragment's Lifecycle is moved to STARTED.
    When the fragment becomes STARTED, the onStart() callback is invoked.
    * */
  }

  /* When your activity is no longer visible to the user, it has entered the Stopped state,
   *  and the system invokes the onStop() callback. This may occur, for example,
   *  when a newly launched activity covers the entire screen. The system may also call onStop()
   *  when the activity has finished running, and is about to be terminated.
   * */
  @Override
  public void onStop() {
    super.onStop();
    Timber.d("breadcrumb");
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(ARG_PARAM1, this.uid);
    Timber.i("Saved instance state");
  }

  /* After all of the exit animations and transitions have completed, and the
  fragment's view has been detached from the window, the fragment's view Lifecycle is
  moved into the DESTROYED state and emits the ON_DESTROY event to its observers. The
  fragment then invokes its onDestroyView() callback. At this point, the fragment's view
  has reached the end of its lifecycle and getViewLifecycleOwnerLiveData() returns a null value.
  At this point, all references to the fragment's view should be removed,
  allowing the fragment's view to be garbage collected.
  */
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Timber.d("breadcrumb");
  }

  /* onDetach() is always called after any Lifecycle state changes. */
  @Override
  public void onDetach() {
    super.onDetach();
    Timber.d("breadcrumb");
  }

  private void setProfilePic(User user, View view) {
    // insert image into profile pic view
    StorageReference profilePicRef = FirebaseStorageRepository.getInstance().get(user.getProfilePicUrl());

    profilePicRef.getDownloadUrl().addOnCompleteListener(task -> {
      ImageView profilePic = view.findViewById(R.id.profile_picture);

      Glide.with(profilePic.getContext())
              .load(profilePicRef)
              .placeholder(R.drawable.person_photo)
              .circleCrop()
              .into(profilePic);

    }).addOnFailureListener(error -> Timber.w("Could not fetch profile pic"));
  }

  private void setFollowerCount(User user) {
    try {
      followerCountView.setText(String.valueOf(user.getFollowers().size()));
      Timber.i("Set follower count");
    } catch (Exception exc) {
      Timber.w("Could not set follower count");
      Timber.e(exc);
    }
  }

  private void setFollowingCount(User user) {
    try {
      followingCountView.setText(String.valueOf(user.getFollowing().size()));
      Timber.i("Set follow count");
    } catch (Exception exc) {
      Timber.w("Could not set follow count");
      Timber.e(exc);
    }
  }

  private void setFollowButton(User user, View view) {
    assert this.uid != null && user.getId() != null;

    String loggedInUserUid = FirebaseAuthLiveData.getInstance().getAuth().getUid();
    followerCountView = view.findViewById(R.id.profile_primary_text);

    if (user.getFollowers().stream().anyMatch(follower -> follower.getId().equals(loggedInUserUid))) {
      // `loggedInUserUid` already follows `user`
      followButton.setText("Unfollow");
      followButton.setOnClickListener(
              view1 -> {
                // update following and followers
                UserRepository.getInstance().unfollow(loggedInUserUid, user.getId());
                followButton.setEnabled(false);
                Timber.i("Follow event recorded: %s -> %s", loggedInUserUid, user.getId());
              }
      );
    } else {
      // `loggedInUserUid` does not follow `user`
      if (user.getBlockedBy().stream().anyMatch(userRef -> userRef.getId().equals(loggedInUserUid))) {
        // `loggedInUserUid` has blocked `user`, so disable follow button
        followButton.setEnabled(false);
      } else {
        followButton.setOnClickListener(
                view1 -> {
                  UserRepository.getInstance().follow(loggedInUserUid, user.getId());
                  followButton.setEnabled(false);
                  Timber.i("Follow event recorded: %s -> %s", loggedInUserUid, user.getId());
                }
        );
      }
    }
  }

  private void setBlockButton(User user, View view) {
    assert this.uid != null && user.getId() != null;

    String loggedInUserUid = FirebaseAuthLiveData.getInstance().getAuth().getUid();
    blockButton = view.findViewById(R.id.profile_block_button);

    if (user.getBlockedBy().stream().anyMatch(blocker -> blocker.getId().equals(loggedInUserUid))) {
      // `loggedInUserUid` already blocked `user`
      blockButton.setText("Unblock");
      blockButton.setOnClickListener(
              view1 -> {
                UserRepository.getInstance().unblock(loggedInUserUid, user.getId());
                blockButton.setEnabled(false);
                Timber.i("Unblock event recorded: %s -> %s", loggedInUserUid, user.getId());
              }
      );
    } else {
      // not blocked by `user`
      blockButton.setOnClickListener(
              view1 -> {
                // block user, and unfollow if following
                UserRepository.getInstance().block(loggedInUserUid, user.getId());

                // disable block button when it's pressed
                blockButton.setEnabled(false);

                // update button label and enabled status if needed
                if (followButton.getText().toString().equals("Unfollow"))
                  followButton.setText("Follow");
                if (!followButton.isEnabled()) followButton.setEnabled(false);

                // update follower count
                int count = Integer.parseInt(followerCountView.getText().toString());
                followerCountView.setText(String.valueOf(count - 1));

                Timber.i("Block event recorded: %s -> %s", loggedInUserUid, user.getId());
              }
      );
    }
  }

  public void logOut() {
    if (mAuth.getCurrentUser() != null) mAuth.signOut();
    Timber.i("Taking user to sign-in screen");
    startActivity(new Intent(getActivity(), LogInActivity.class));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    //detach listeners when Activity destroyed
    firebaseListenerRegs.forEach(ListenerRegistration::remove);
  }

  public void renderProfile(User profileUser, View view) {
    if (profileUser == null) {
      Timber.i("profileUser is null, this.uid=%s", ProfileFragment.this.uid);
      return;
    }

    uid = profileUser.getId();

    // fill in user info
    userNameView.setText(profileUser.getUserName());
    ProfileFragment.this.setProfilePic(profileUser, view);
    ProfileFragment.this.setFollowerCount(profileUser);
    ProfileFragment.this.setFollowingCount(profileUser);
    ProfileFragment.this.setFollowButton(profileUser, view);
    ProfileFragment.this.setBlockButton(profileUser, view);

    User liveUser = viewModel.getLiveUser().getValue();
    assert liveUser != null && liveUser.getId() != null;
    Timber.i("Firebase current uid: %s\t\tthis.uid: %s", liveUser.getId(), uid);
    Timber.i("Current User variable: %s", liveUser);

    Button signOutButton = view.findViewById(R.id.sign_out_button);
    int visibility;

    // if a user is looking at his/her own profile, hide Follow and Message buttons.
    if (liveUser.getId().equals(profileUser.getId())) {
      Timber.i("Viewing self's profile. Hiding Follow and Message buttons.");
      visibility = View.INVISIBLE;
      // show sign out button if looking at self profile
      signOutButton.setOnClickListener(unused -> ProfileFragment.this.logOut());
      signOutButton.setEnabled(true);
    } else {
      visibility = View.VISIBLE;
      signOutButton.setVisibility(View.INVISIBLE);
      signOutButton.setEnabled(false);
    }

    // hide follow, message, block buttons if looking at self's profile
    // TODO: delete these parts entirely to expand the view
    view.findViewById(R.id.profile_follow_button).setVisibility(visibility);
    view.findViewById(R.id.profile_message_button).setVisibility(visibility);
    view.findViewById(R.id.profile_block_button).setVisibility(visibility);
  }
}