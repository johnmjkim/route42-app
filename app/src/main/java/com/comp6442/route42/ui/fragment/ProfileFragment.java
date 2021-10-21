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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.ui.viewmodel.LiveUserViewModel;
import com.comp6442.route42.ui.viewmodel.ProfileUserViewModel;
import com.comp6442.route42.ui.viewmodel.UserViewModel;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.adapter.FirestorePostAdapter;
import com.comp6442.route42.ui.activity.LogInActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ProfileFragment extends Fragment {
  private static final String ARG_PARAM1 = "uid";
  private final List<ListenerRegistration> firebaseListenerRegs = new ArrayList<>();
  private FirebaseAuth mAuth;
  private String uid;
  private LiveUserViewModel liveUserVM ;
  private ProfileUserViewModel profileUserVM;
  private TextView userNameView, followerCountView, followingCountView;
  private SwitchMaterial blockSwitch, followSwitch;
  private MaterialButton messageButton, signOutButton, showBlockedUsersButton;
  private NestedScrollView scrollview;
  private RecyclerView recyclerView;
  private FirestorePostAdapter adapter;
  private LinearLayoutManager layoutManager;
  private BottomNavigationView bottomNavView;

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
    requireActivity().findViewById(R.id.Btn_Create_Activity).setVisibility(View.VISIBLE);
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Timber.d("breadcrumb");

    // BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    // bottomNavView.animate().translationY(0).setDuration(250);

    liveUserVM = new ViewModelProvider(requireActivity()).get(LiveUserViewModel.class);
    profileUserVM = new ViewModelProvider(requireActivity()).get(ProfileUserViewModel.class);

    // set view variables
    userNameView = view.findViewById(R.id.profile_username);
    blockSwitch = view.findViewById(R.id.profile_block_switch);
    followSwitch = view.findViewById(R.id.profile_follow_switch);
    messageButton = view.findViewById(R.id.profile_message_button);
    signOutButton = view.findViewById(R.id.sign_out_button);
    followerCountView = view.findViewById(R.id.profile_primary_text);
    followingCountView = view.findViewById(R.id.profile_secondary_text);
    showBlockedUsersButton = view.findViewById(R.id.show_blocked_users_button);

    if (savedInstanceState != null) {
      //Restore the fragment's state here
      this.uid = savedInstanceState.getString(ARG_PARAM1);
      Timber.d("Restoring fragment state for uid: %s", this.uid);
    }

    if (this.uid != null) {
      if (this.uid.contains("\"")) this.uid = this.uid.replaceAll("^\"|\"$", "");
      Timber.i("Received uid: %s", this.uid);

      // create observer to update the profile UI on change to the `ProfileUser`
      final Observer<User> userObserver = updatedProfileUser -> {
        Timber.i("userObserver notified: %s", updatedProfileUser);
        if (updatedProfileUser == null) return;
        User currentProfileUser = this.profileUserVM.getUser().getValue();
        assert currentProfileUser != null;
        if (currentProfileUser.getId().equals(updatedProfileUser.getId())) {
          renderProfile(updatedProfileUser, view);
        }
        renderRecyclerView(updatedProfileUser, view);
      };

      // initialize profileUser, observe change to the profileUser data, and get a registration
      profileUserVM.loadProfileUser(this.uid);
      profileUserVM.getUser().observe(getViewLifecycleOwner(), userObserver);
      firebaseListenerRegs.add(profileUserVM.addSnapshotListener(this.uid));
    } else {
      Timber.w("uid is null");
    }
  }
  /* It is strongly recommended to tie Lifecycle-aware components to the STARTED state of a
      fragment, as this state guarantees that the fragment's view is available, if one was created,
      and that it is safe to perform a FragmentTransaction on the child FragmentManager of the fragment.
      If the fragment's view is non-null, the fragment's view Lifecycle is moved to STARTED
      immediately after the fragment's Lifecycle is moved to STARTED.
      When the fragment becomes STARTED, the onStart() callback is invoked.
      * */
  @Override
  public void onStart() {
    super.onStart();
    Timber.d("breadcrumb");

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
    if (adapter != null) adapter.stopListening();
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

  @Override
  public void onDestroy() {
    firebaseListenerRegs.forEach(ListenerRegistration::remove);
    super.onDestroy();
    //detach listeners when Activity destroyed
  }

  /* onDetach() is always called after any Lifecycle state changes. */
  @Override
  public void onDetach() {
    super.onDetach();
    firebaseListenerRegs.forEach(ListenerRegistration::remove);
    Timber.d("breadcrumb");
  }

  private void setProfilePic(User user, View view) {
    // TODO: enable cache
    ImageView profilePic = view.findViewById(R.id.profile_picture);
    Timber.i(user.toString());
    if (user.getProfilePicUrl() != null) {
      if (user.getProfilePicUrl().startsWith("http")) {
        Glide.with(profilePic.getContext())
                .load(user.getProfilePicUrl())
                .placeholder(R.drawable.unknown_user)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(false)
                .circleCrop()
                .into(profilePic);
      } else {
        // Get reference to the image file in Cloud Storage, download route image, use stock photo if fail
        StorageReference profilePicRef = FirebaseStorageRepository.getInstance().get(user.getProfilePicUrl());

        Glide.with(profilePic.getContext())
                .load(profilePicRef)
                .placeholder(R.drawable.unknown_user)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(false)
                .circleCrop()
                .into(profilePic);
      }
    }
  }

  private void setFollowerCount(User user) {
    try {
      followerCountView.setText(String.valueOf(user.getFollowers().size()));
      followerCountView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Fragment fragment = new UserListFragment();
          Bundle bundle = new Bundle();

          bundle.putString("uid", user.getId());
          bundle.putString("fieldName", "followers");
          fragment.setArguments(bundle);
          ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                  .beginTransaction()
                  .add(R.id.fragment_container_view, fragment)
                  .addToBackStack(this.getClass().getCanonicalName())
                  .commit();
        }
      });
      Timber.i("Set follower count");
    } catch (Exception exc) {
      Timber.w("Could not set follower count");
      Timber.e(exc);
    }
  }

  private void setFollowingCount(User user) {
    try {
      followingCountView.setText(String.valueOf(user.getFollowing().size()));
      followingCountView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Fragment fragment = new UserListFragment();
          Bundle bundle = new Bundle();

          bundle.putString("uid", user.getId());
          bundle.putString("fieldName", "following");
          fragment.setArguments(bundle);
          ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                  .beginTransaction()
                  .add(R.id.fragment_container_view, fragment)
                  .addToBackStack(this.getClass().getCanonicalName())
                  .commit();
        }
      });
      Timber.i("Set follow count");
    } catch (Exception exc) {
      Timber.w("Could not set follow count");
      Timber.e(exc);
    }
  }

  private void setFollowSwitch(User user) {
    assert this.uid != null && user.getId() != null;
    String loggedInUserUid = liveUserVM.getUser().getValue().getId();

//    String loggedInUserUid = FirebaseAuthLiveData.getInstance().getAuth().getUid();
    followSwitch.setOnCheckedChangeListener(null);

    // check if loggedInUser already follows profileUser
    followSwitch.setChecked(
            user.getFollowers().stream().anyMatch(
                    follower -> follower.getId().equals(loggedInUserUid)));

    followSwitch.setOnCheckedChangeListener((compoundButton, isOn) -> {
      if (isOn) {
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
    String loggedInUserUid = liveUserVM.getUser().getValue().getId();
//    String loggedInUserUid = FirebaseAuthLiveData.getInstance().getAuth().getUid();
    blockSwitch.setOnCheckedChangeListener(null);

    // check if loggedInUser already blocked profileUser
    blockSwitch.setChecked(
            user.getBlockedBy().stream().anyMatch(
                    blocker -> blocker.getId().equals(loggedInUserUid)));

    if (blockSwitch.isChecked()) {
      followSwitch.setEnabled(false);
    }

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
    User liveUser = liveUserVM.getUser().getValue();
    assert liveUser != null && liveUser.getId() != null;
    Timber.i("Firebase current uid: %s\t\tthis.uid: %s", liveUser.getId(), uid);
    Timber.i("Current LiveUser: %s", liveUser);
    Timber.i("Current ProfileUser: %s", profileUserVM.getUser().getValue());

    int visibility;

    // if a user is looking at his/her own profile, hide Follow and Message buttons, show Logout button and blocked users
    // TODO: delete these parts entirely instead of setting to invisible
    if (liveUser.getId().equals(profileUser.getId())) {
      Timber.i("Viewing self's profile. Hiding Follow and Message buttons.");
      visibility = View.GONE;
      signOutButton.setEnabled(true);
      signOutButton.setOnClickListener(unused -> ProfileFragment.this.logOut());
      signOutButton.setVisibility(View.VISIBLE);
      showBlockedUsersButton.setVisibility(View.VISIBLE);
      showBlockedUsersButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Fragment fragment = new UserListFragment();
          Bundle bundle = new Bundle();

          bundle.putString("uid", liveUser.getId());
          bundle.putString("fieldName", "blocked");
          fragment.setArguments(bundle);
          ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                  .beginTransaction()
                  .add(R.id.fragment_container_view, fragment)
                  .addToBackStack(this.getClass().getCanonicalName())
                  .commit();
        }
      });
    } else {
      visibility = View.VISIBLE;
      signOutButton.setEnabled(false);
      signOutButton.setVisibility(View.GONE);
      showBlockedUsersButton.setVisibility(View.GONE);
    }
    followSwitch.setVisibility(visibility);
    blockSwitch.setVisibility(visibility);
    messageButton.setVisibility(visibility);
  }

  private void renderRecyclerView(User user, View view) {
    Timber.i("Rendering feed by user: %s", user);
    Query query = PostRepository.getInstance().getMany(user.getId(), 20);
    FirestoreRecyclerOptions<Post> postsOptions = new FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post.class)
            .build();

    adapter = new FirestorePostAdapter(postsOptions, liveUserVM.getUser().getValue().getId());
    layoutManager = new LinearLayoutManager(getActivity());
    layoutManager.setReverseLayout(false);
    layoutManager.setStackFromEnd(false);

    recyclerView = view.findViewById(R.id.profile_recycler_view);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.setHasFixedSize(false);
    recyclerView.setNestedScrollingEnabled(false);

    scrollview = view.findViewById(R.id.profile_scroll_view);
    scrollview.setSmoothScrollingEnabled(true);

    adapter.startListening();

    Timber.i("FirestorePostAdapter bound to RecyclerView with size %d", adapter.getItemCount());
    query.get().addOnSuccessListener(queryDocumentSnapshots -> Timber.i("%d items found", queryDocumentSnapshots.getDocuments().size()));

    // hide search view on scroll
    bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
          if (dy > 0)
            bottomNavView.animate().translationY(bottomNavView.getHeight()).setDuration(1000); // scrolling down
          else bottomNavView.animate().translationY(0).setDuration(1000); // scrolling up
        }
      }

      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }
    });
  }
}