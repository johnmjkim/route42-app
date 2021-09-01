package com.comp6442.groupproject.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.comp6442.groupproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    mAuth = FirebaseAuth.getInstance();
    mAuth.useEmulator("10.0.2.2", 9099);
  }

  @Override
  public void onStart() {
    super.onStart();

    FirebaseUser user = mAuth.getCurrentUser();

    if (user != null) {
      TextView txtView = (TextView) findViewById(R.id.usernameHome);
      txtView.setText(String.format("Hello, %s", user.getEmail()));
    } else {
      Intent intent = new Intent(this, LogInActivity.class);
      startActivity(intent);
    }
  }
}