package com.comp6442.groupproject.ui;

// taken from https://firebase.google.com/docs/auth/android/start#check_current_auth_state

/*
 * Copyright 2021 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.comp6442.groupproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends Activity implements View.OnClickListener{

  private static final String TAG = "EmailPassword";
  private FirebaseAuth mAuth;
  EditText ed1,ed2;
  Button b1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Initialize Firebase Auth
    // 10.0.2.2 is the special IP address to connect to the 'localhost' of
    // the host computer from an Android emulator.
    mAuth = FirebaseAuth.getInstance();
    mAuth.useEmulator("10.0.2.2", 9099);

    setContentView(R.layout.activity_login);

    ed1 = (EditText)findViewById(R.id.username);
    ed2 = (EditText)findViewById(R.id.password);
    b1 = (Button)findViewById(R.id.login);
    b1.setEnabled(true);
    b1.setOnClickListener(LogInActivity.this);
  }

  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();

    if(currentUser != null){
      reload();
    } else {
      Log.i(TAG, "User already logged in. Taking user to home..");
      // take user to app home screen
    }
  }

  @Override
  public void onClick(View view) {
    String username = ed1.getText().toString();
    String password = ed2.getText().toString();
    FirebaseAuthSettings settings = mAuth.getFirebaseAuthSettings();

    Log.i(TAG, username);
    Log.i(TAG, password);
    signIn(username, password);
  }

  private void createAccount(String email, String password) {
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  // Sign in success, update UI with the signed-in user's information
                  Log.d(TAG, "createUserWithEmail:success");
                  FirebaseUser user = mAuth.getCurrentUser();
                  updateUI(user);
                } else {
                  // If sign in fails, display a message to the user.
                  Log.w(TAG, "createUserWithEmail:failure", task.getException());
                  Toast.makeText(LogInActivity.this, "Authentication failed.",
                          Toast.LENGTH_SHORT).show();
                  updateUI(null);
                }
              }
            });
  }

  private void signIn(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  // Sign in success, update UI with the signed-in user's information
                  Log.d(TAG, "signInWithEmail:success");
                  FirebaseUser user = mAuth.getCurrentUser();
                  updateUI(user);
                } else {
                  // If sign in fails, display a message to the user.
                  Log.w(TAG, "signInWithEmail:failure", task.getException());
                  Toast.makeText(LogInActivity.this, "Authentication failed.",
                          Toast.LENGTH_SHORT).show();
                  updateUI(null);
                }
              }
            });
  }

//  private void sendEmailVerification() {
//    final FirebaseUser user = mAuth.getCurrentUser();
//    user.sendEmailVerification()
//            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//              @Override
//              public void onComplete(@NonNull Task<Void> task) {
//                // Email sent
//              }
//            });
//  }

  private void reload() {
    // take user back to log in screen
  }

  private void updateUI(FirebaseUser user) {
    Log.i(TAG, String.format("Sign in successful: %s", user));
    Log.i(TAG, mAuth.getCurrentUser().toString());

    Toast.makeText(LogInActivity.this,
            "Redirecting..." + user.toString(), Toast.LENGTH_SHORT).show();
  }
}