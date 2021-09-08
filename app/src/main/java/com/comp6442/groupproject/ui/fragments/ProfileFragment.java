package com.comp6442.groupproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.comp6442.groupproject.BuildConfig;
import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.comp6442.groupproject.ui.LogInActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
  private static final String ARG_PARAM1 = "uid";
  private FirebaseAuth mAuth;
  private ListenerRegistration registration;
  private String uid;
  private TextView userNameView;

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
  // TODO: Rename and change types and number of parameters
  public static ProfileFragment newInstance(String param1) {
    ProfileFragment fragment = new ProfileFragment();
    Timber.d("created with param %s", param1);
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      this.uid = getArguments().getString(ARG_PARAM1);
    }

    this.mAuth = FirebaseAuth.getInstance();
    if (BuildConfig.DEBUG) {
      try {
        this.mAuth.useEmulator("10.0.2.2", 9099);
      } catch (IllegalStateException exc) {
        Timber.d(exc);
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    userNameView = view.findViewById(R.id.profile_username);

    if (savedInstanceState != null) {
      //Restore the fragment's state here
      this.uid = savedInstanceState.getString("uid");
    }

    FirebaseUser firebaseUser = this.mAuth.getCurrentUser();
    if (firebaseUser == null) logOut();

    if (this.uid != null) {
      Timber.d("Received uid: %s", this.uid);
      Timber.d("Retrieved Firebase user %s", firebaseUser.getUid());

      // insert username to profile
      String uid = this.uid;
      UserRepository.getInstance().getUser(uid).get().addOnSuccessListener(snapshot -> {
        User user = snapshot.toObject(User.class);
        if (user != null) {
          userNameView.setText(user.getUserName());
          Timber.d("Retrieved user from Firestore: %s", user);
        }
      }).addOnFailureListener(e -> Timber.w("Could not obtain user from Firestore: %s", uid));

      // when a user is looking at his/her own profile, hide Follow and Message buttons.
      if (firebaseUser.getUid().equals(this.uid)) {
        Timber.d("Fetching logged in user's profile. Hiding Follow and Message buttons.");
        view.findViewById(R.id.profile_follow_button).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_message_button).setVisibility(View.INVISIBLE);
      }

      Button signOutButton = view.findViewById(R.id.sign_out_button);
      signOutButton.setOnClickListener(unused -> logOut());
      signOutButton.setEnabled(true);
    }
  }

  private void registerListener() {
    // attach a listener and update username in realtime
    this.registration = UserRepository.getInstance().getUser(this.uid).addSnapshotListener((snapshot, error) -> {
      if (error != null) {
        Timber.w(error);
        return;
      }

      String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
      String userName = (String) Objects.requireNonNull(snapshot).get("userName");
      if (userName != null) {
        Timber.d(source + " data: " + userName);
        userNameView.setText(String.format("Username: %s", userName));
      } else {
        Timber.w("%s data: null", source);
      }
    });
  }

  @Override
  public void onStop() {
    super.onStop();
    if (registration != null) registration.remove();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(ARG_PARAM1, this.uid);
    if (registration != null) registration.remove();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (registration != null) registration.remove();
  }

  public void logOut() {
    if (this.mAuth.getCurrentUser() != null) this.mAuth.signOut();
    if (registration != null) registration.remove();
    Timber.i("Taking user to sign-in screen");
    startActivity(new Intent(getActivity(), LogInActivity.class));
  }
}