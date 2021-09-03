package com.comp6442.groupproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
  private static final String TAG = "HomeActivity";
  Button b1;
  TextView txtView;
  private FirebaseAuth mAuth;
  private DocumentReference userDoc;
  private ListenerRegistration registration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance();

    // 10.0.2.2 is the special IP address to connect to the 'localhost' of
    // the host computer from an Android emulator.
    mAuth.useEmulator("10.0.2.2", 9099);

    b1 = findViewById(R.id.signout);
    b1.setOnClickListener(view -> {
      FirebaseUser firebaseUser = mAuth.getCurrentUser();
      Log.i(TAG, String.format("Signing out: %s", firebaseUser));
      mAuth.signOut();
      logOut();
    });
    b1.setEnabled(true);
    txtView = findViewById(R.id.usernameHome);
  }

  @Override
  public void onStart() {
    super.onStart();

    // get the get Intent object
    Intent intent = getIntent();

    // receive the value by getStringExtra() method - keys must match
    String uid = intent.getStringExtra("uid");
    this.userDoc = UserRepository.getInstance().getUser(uid);
    Log.i(TAG, uid);

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

        txtView.setText(String.format("Hello, %s", user.getUserName()));
        Log.i(TAG, String.format("User successfully fetched: %s", user));
      } else {
        Log.d(TAG, source + " data: null");
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    registration.remove();
  }

  public void logOut() {
    // take user to app home screen
    Log.i(TAG, "Taking user to sign-in screen");
    startActivity(new Intent(this, LogInActivity.class));
  }
}