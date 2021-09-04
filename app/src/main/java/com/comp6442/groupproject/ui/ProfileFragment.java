package com.comp6442.groupproject.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.comp6442.groupproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
  private static final String TAG = ProfileFragment.class.getCanonicalName();
  private FirebaseAuth mAuth;

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "uid";
  private Button b1;

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
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // sign out button
    b1 = view.findViewById(R.id.sign_out_button);
    b1.setOnClickListener(view1 -> {
      FirebaseUser firebaseUser = mAuth.getCurrentUser();
      Log.i(TAG, String.format("Signing out: %s", firebaseUser));
      mAuth.signOut();
      logOut();
    });
    b1.setEnabled(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  public void logOut() {
    Log.i(TAG, "Taking user to sign-in screen");
    startActivity(new Intent(getActivity(), LogInActivity.class));
  }
}