package com.comp6442.route42.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
  private SwitchMaterial blockSwitch, followSwitch;
  private MaterialButton messageButton, signOutButton;

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
    blockSwitch = view.findViewById(R.id.profile_block_switch);
    followSwitch = view.findViewById(R.id.profile_follow_switch);
    messageButton = view.findViewById(R.id.profile_message_button);
    signOutButton = view.findViewById(R.id.sign_out_button);
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
    if (user.getProfilePicUrl() != null) {
      // insert image into profile pic view
      StorageReference profilePicRef = FirebaseStorageRepository.getInstance().get(user.getProfilePicUrl());

      profilePicRef.getDownloadUrl().addOnCompleteListener(task -> {
        ImageView profilePic = view.findViewById(R.id.profile_picture);
        Glide.with(profilePic.getContext())
                .load(profilePicRef)
                .placeholder(R.drawable.person_photo)
                .circleCrop()
                .into(profilePic);
      });
    }
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

  private void setFollowSwitch(User user) {
    assert this.uid != null && user.getId() != null;

    String loggedInUserUid = FirebaseAuthLiveData.getInstance().getAuth().getUid();

    // check if loggedInUser already follows profileUser
    followSwitch.setChecked(
            user.getFollowers().stream().anyMatch(
                    follower -> follower.getId().equals(loggedInUserUid)));

    followSwitch.setOnCheckedChangeListener((compoundButton, isOn) -> {
      if(isOn) {
        // follow action triggers unblock
        Timber.i("Follow event recorded: %s -> %s", loggedInUserUid, user.getId());
        UserRepository.getInstance().follow(loggedInUserUid, user.getId());
      } else {
        Timber.i("UnFollow event recorded: %s -> %s", loggedInUserUid, user.getId());
        UserRepository.getInstance().unfollow(loggedInUserUid, user.getId());
      }
    });

  }

  private void setBlockSwitch(User user) {
    assert this.uid != null && user.getId() != null;

    String loggedInUserUid = FirebaseAuthLiveData.getInstance().getAuth().getUid();

    // check if loggedInUser already blocked profileUser
    blockSwitch.setChecked(
            user.getBlockedBy().stream().anyMatch(
                    blocker -> blocker.getId().equals(loggedInUserUid)));

    blockSwitch.setOnCheckedChangeListener((compoundButton, isOn) -> {
      if (isOn) {
        // .block(loggedInUserUid, user.getId()) is automatically followed by unfollow (if following before blocking)
        Timber.i("Block event recorded: %s -> %s", loggedInUserUid, user.getId());
        UserRepository.getInstance().block(loggedInUserUid, user.getId());
        if (followSwitch.isChecked()) {
          followSwitch.setOnCheckedChangeListener(null);
          followSwitch.setChecked(false);
          setFollowSwitch(user);
        }
        followSwitch.setEnabled(false);
      } else {
        Timber.i("Unblock event recorded: %s -> %s", loggedInUserUid, user.getId());
        UserRepository.getInstance().unblock(loggedInUserUid, user.getId());
        followSwitch.setEnabled(true);
      }
    });
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
    setProfilePic(profileUser, view);
    setFollowerCount(profileUser);
    setFollowingCount(profileUser);
    setFollowSwitch(profileUser);
    setBlockSwitch(profileUser);

    User liveUser = viewModel.getLiveUser().getValue();
    assert liveUser != null && liveUser.getId() != null;
    Timber.i("Firebase current uid: %s\t\tthis.uid: %s", liveUser.getId(), uid);
    Timber.i("Current LiveUser: %s", liveUser);
    Timber.i("Current ProfileUser: %s", viewModel.getProfileUser().getValue());

    int visibility;

    // if a user is looking at his/her own profile, hide Follow and Message buttons.
    // TODO: delete these parts entirely instead of setting to invisible
    if (liveUser.getId().equals(profileUser.getId())) {
      Timber.i("Viewing self's profile. Hiding Follow and Message buttons.");
      visibility = View.INVISIBLE;
      signOutButton.setEnabled(true);
      signOutButton.setOnClickListener(unused -> ProfileFragment.this.logOut());
      signOutButton.setVisibility(View.VISIBLE);
    } else {
      visibility = View.VISIBLE;
      signOutButton.setEnabled(false);
      signOutButton.setVisibility(View.INVISIBLE);
    }

    followSwitch.setVisibility(visibility);
    blockSwitch.setVisibility(visibility);
    messageButton.setVisibility(visibility);
  }
}