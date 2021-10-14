package com.comp6442.route42.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.utils.AESCrypt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import timber.log.Timber;


public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
  private EditText ed1, ed2;
  private Button b1;
  private FirebaseAuth mAuth;

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    mAuth = FirebaseAuthLiveData.getInstance().getAuth();

    ed1 = findViewById(R.id.login_form_email);
    ed2 = findViewById(R.id.login_form_password);
    b1 = findViewById(R.id.login_button);
    b1.setEnabled(true);
    b1.setOnClickListener(LogInActivity.this);

    // ActionBar toolbar = getSupportActionBar();
    // toolbar.hide();
  }

  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser user = mAuth.getCurrentUser();

    if (user != null) {
      Timber.d("User already logged in: %s. Taking user to home..", user.getEmail());
      home(user);
    }
  }

  @Override
  public void onClick(View view) {
    String username = ed1.getText().toString();
    String password = ed2.getText().toString();

    try {
      signIn(username, AESCrypt.encrypt(password));
    } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
      Timber.e(e);
    }
  }

  @SuppressLint("TimberArgCount")
  private void signIn(String email, String password)  {
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                if (firebaseUser != null) {
                  Timber.d("Sign in successful: %s", firebaseUser.getEmail());
                  home(firebaseUser);
                }
              } else {
                Timber.w(task.getException(), "Failed to sign in");
                ed2.setText(" ");
                ed2.setText("");
              }
            });
  }

  @SuppressLint("TimberArgCount")
  private void createAccount(String email, String password) {
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                Timber.i("Successfully created account: %s", Objects.requireNonNull(firebaseUser).getEmail());
                Toast.makeText(LogInActivity.this, "Success", Toast.LENGTH_SHORT).show();
                UserRepository.getInstance().createOne(firebaseUser);
                home(firebaseUser);
              } else {
                // If sign in fails, display a message to the user.
                Timber.w(task.getException(), "Failed to create account");
                Toast.makeText(LogInActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
              }
            });
  }

  private void home(FirebaseUser firebaseUser) {
    if (firebaseUser == null) Timber.w("Error, could not fetch current user");
    else {
      // take user to app home screen
      Timber.d("Taking user to app home screen %s", firebaseUser.getUid());
      Intent intent = new Intent(this, MainActivity.class);
      intent.putExtra("uid", firebaseUser.getUid());
      startActivity(intent);
    }
  }
}