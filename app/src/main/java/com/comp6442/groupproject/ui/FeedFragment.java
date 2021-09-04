package com.comp6442.groupproject.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
  private static final String TAG = FeedFragment.class.getCanonicalName();
  private DocumentReference userDoc;
  private ListenerRegistration registration;
  private TextView welcomeMessage;

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "uid";

  private String uid;

  public FeedFragment() {
    // Required empty public constructor
  }

  public static FeedFragment newInstance(String uid) {
    FeedFragment frag = new FeedFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, uid);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      uid = getArguments().getString(ARG_PARAM1);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (savedInstanceState == null) {
      Bundle args = getArguments();
      if (args != null) {
        this.uid = args.getString(ARG_PARAM1);
      } else {
        Log.w(TAG, "Could not obtain uid");
      }
    } else {
      this.uid = savedInstanceState.getString(ARG_PARAM1);
    }

    if (this.uid != null) {
      // receive the value by getStringExtra() method - keys must match
      this.userDoc = UserRepository.getInstance().getUser(this.uid);
      Log.i(TAG, this.uid);

      registration = this.userDoc.addSnapshotListener((snapshot, error) -> {
        if (error != null) {
          Log.w(TAG, "Listen failed.", error);
          return;
        }

        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
        if (snapshot != null && snapshot.exists()) {
          Log.d(TAG, source + " data: " + snapshot.getData());

          User user = new User(
                  (String) Objects.requireNonNull(snapshot.get("uid")),
                  (String) Objects.requireNonNull(snapshot.get("email"))
          );
          user.setUserName((String) snapshot.get("userName"));

          Log.i(TAG, String.format("User successfully fetched: %s", user));
        } else {
          Log.d(TAG, source + " data: null");
        }
      });
      welcomeMessage = view.findViewById(R.id.feed_welcome_txt);
      welcomeMessage.setText(String.format("Hello, %s", this.uid));
    } else {
      Log.d(TAG, "not signed in");
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
    registration.remove();
  }
}