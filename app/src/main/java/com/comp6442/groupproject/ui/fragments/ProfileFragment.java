package com.comp6442.groupproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.comp6442.groupproject.ui.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
  private static final String TAG = ProfileFragment.class.getCanonicalName();
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "uid";
  private FirebaseAuth mAuth;
  private Button b1;
  private TextView userNameTxt, emailTxt;

  // TODO: Rename and change types of parameters
  private String mParam1;

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
    Log.d(TAG, "created with param " + param1);
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
    }

    // Initialize Firebase Auth
    // 10.0.2.2 is the special IP address to connect to the 'localhost' of
    // the host computer from an Android emulator.
    mAuth = FirebaseAuth.getInstance();
    mAuth.useEmulator("10.0.2.2", 9099);
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

    // user info
    userNameTxt = (TextView) view.findViewById(R.id.userName);
    emailTxt = (TextView) view.findViewById(R.id.email);

    if (savedInstanceState != null) {
      //Restore the fragment's state here
      mParam1 = savedInstanceState.getString("uid");
    }

    FirebaseUser firebaseUser = mAuth.getCurrentUser();
    String uid = mParam1;
    if (uid != null && firebaseUser != null) {
      Log.d(TAG, "Received uid: " + uid);
      Log.d(TAG, "Retrieved Firebase user " + firebaseUser.getUid());

      DocumentReference userDocument = UserRepository.getInstance().getUser(uid);
      userDocument.get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          String userName = task.getResult().getString("userName");
          Log.d(TAG, "Retrieved user from Firestore: " + userName);
          userNameTxt.setText(String.format("Username: %s", userName));
          emailTxt.setText(String.format("Email: %s", task.getResult().getString("email")));
        } else {
          Log.w(TAG, "Could not obtain user from Firestore: " + uid);
        }
      });
    }

    // sign out button
    b1 = view.findViewById(R.id.sign_out_button);
    b1.setOnClickListener(view1 -> logOut());
    b1.setEnabled(true);

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putString(ARG_PARAM1, mParam1);
    super.onSaveInstanceState(outState);
  }

  public void logOut() {
    if (mAuth.getCurrentUser() != null) mAuth.signOut();
    Log.i(TAG, "Taking user to sign-in screen");
    startActivity(new Intent(getActivity(), LogInActivity.class));
  }
}