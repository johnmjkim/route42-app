package com.comp6442.groupproject.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = "LogIn";
  EditText ed1, ed2;
  Button b1;
  private FirebaseAuth mAuth;

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance();

    // 10.0.2.2 is the special IP address to connect to the 'localhost' of
    // the host computer from an Android emulator.
    mAuth.useEmulator("10.0.2.2", 9099);

    ed1 = findViewById(R.id.login_form_email);
    ed2 = findViewById(R.id.login_form_password);
    b1 = findViewById(R.id.login_button);
    b1.setEnabled(true);
    b1.setOnClickListener(LogInActivity.this);

    // for testing
    mAuth.createUserWithEmailAndPassword("foo@bar.com", "password")
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                Log.d(TAG, "Created test user.");
              } else {
                Log.d(TAG, "Test user already exists.");
              }
            });

    mAuth.signInWithEmailAndPassword("foo@bar.com", "password").addOnCompleteListener(task -> {
      FirebaseUser firebaseUser = Objects.requireNonNull(task.getResult()).getUser();
      if (firebaseUser != null) {
        UserRepository.getInstance().addUser(firebaseUser);
        User user = new User(
                Objects.requireNonNull(firebaseUser.getUid()),
                Objects.requireNonNull(firebaseUser.getEmail())
        );
        user.setUserName("test_user");
        UserRepository.getInstance().updateUser(user);
        mAuth.signOut();
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser user = mAuth.getCurrentUser();

    if (user != null) {
      Log.i(TAG, String.format("User already logged in: %s. Taking user to home..", user.getEmail()));
      home(user);
    }
  }

  @Override
  public void onClick(View view) {
    String username = ed1.getText().toString();
    String password = ed2.getText().toString();
    signIn(username, password);
  }

  private void signIn(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                if (firebaseUser != null) {
                  Log.i(TAG, String.format("Sign in successful: %s", firebaseUser.getEmail()));
                  Toast.makeText(LogInActivity.this, "Success", Toast.LENGTH_SHORT).show();
                  home(firebaseUser);
                }
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "Failed to sign in", task.getException());
                Toast.makeText(LogInActivity.this, "Sign in failed.",
                        Toast.LENGTH_SHORT).show();
                ed2.setText(" ");
                ed2.setText("");
              }
            });
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void createAccount(String email, String password) {
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                Log.i(TAG, String.format("Successfully created account: %s", firebaseUser.getEmail()));
                Toast.makeText(LogInActivity.this, "Success", Toast.LENGTH_SHORT).show();
                UserRepository.getInstance().addUser(firebaseUser);
                home(firebaseUser);
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "Failed to create account", task.getException());
                Toast.makeText(LogInActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
              }
            });
  }

  private void home(FirebaseUser firebaseUser) {
    if (firebaseUser == null) Log.w(TAG, "Error, could not fetch current user");
    else {
      // take user to app home screen
      Log.i(TAG, "Taking user to app home screen " + firebaseUser.getUid());
      Intent intent = new Intent(this, MainActivity.class);
      intent.putExtra("uid", firebaseUser.getUid());
      startActivity(intent);
    }
  }
}