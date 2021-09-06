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

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.comp6442.groupproject.ui.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

    // Initialize Firebase Auth
    // 10.0.2.2 is the special IP address to connect to the 'localhost' of
    // the host computer from an Android emulator.
    this.mAuth = FirebaseAuth.getInstance();
    this.mAuth.useEmulator("10.0.2.2", 9099);
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

    if (savedInstanceState != null) {
      //Restore the fragment's state here
      this.uid = savedInstanceState.getString("uid");
    }

    FirebaseUser firebaseUser = this.mAuth.getCurrentUser();
    if (firebaseUser == null) logOut();

    if (this.uid != null) {
      Timber.d("Received uid: %s", this.uid);
      Timber.d("Retrieved Firebase user %s", firebaseUser.getUid());

      DocumentReference userDocument = UserRepository.getInstance().getUser(this.uid);
      TextView userNameView = view.findViewById(R.id.profile_username);

      // insert username to profile
      userDocument.get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          String userName = task.getResult().getString("userName");
          Timber.d("Retrieved user from Firestore: %s", userName);
          userNameView.setText(String.format("Username: %s", userName));
        } else {
          Timber.w("Could not obtain user from Firestore: %s", this.uid);
        }
      });

      // attach a listener and update username in realtime
      this.registration = userDocument.addSnapshotListener((snapshot, error) -> {
        if (error != null) {
          Timber.w(error);
          return;
        }

        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
        if (snapshot != null && snapshot.exists()) {
          Timber.d(source + " data: " + snapshot.getData());

          User user = new User(
                  (String) Objects.requireNonNull(snapshot.get("uid")),
                  (String) Objects.requireNonNull(snapshot.get("email"))
          );
          user.setUserName((String) snapshot.get("userName"));

          Timber.d("User successfully fetched: %s", user);
          userNameView.setText(String.format("Username: %s", user.getUserName()));
        } else {
          Timber.w("%s data: null", source);
        }
      });

      // when a user is looking at his/her own profile, hide Follow and Message buttons.
      if (firebaseUser.getUid().equals(this.uid)) {
        Timber.d("Fetching logged in user's profile. Hiding Follow and Message buttons.");
        view.findViewById(R.id.profile_follow_button).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.profile_message_button).setVisibility(View.INVISIBLE);
      }

      // sign out button
      Button b1 = view.findViewById(R.id.sign_out_button);
      b1.setOnClickListener(view1 -> logOut());
      b1.setEnabled(true);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putString(ARG_PARAM1, this.uid);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (registration != null) registration.remove();
  }

  public void logOut() {
    if (this.mAuth.getCurrentUser() != null) this.mAuth.signOut();
    Timber.i("Taking user to sign-in screen");
    startActivity(new Intent(getActivity(), LogInActivity.class));
  }
}