package com.comp6442.groupproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.comp6442.groupproject.R;

public class HomeActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
  }

  @Override
  public void onStart() {
    super.onStart();

    // get the get Intent object
    Intent intent = getIntent();

    // receive the value by getStringExtra() method
    // and key must be same which is send by first activity
    String username = intent.getStringExtra("email");

    TextView txtView = (TextView) findViewById(R.id.usernameHome);
    txtView.setText(String.format("Hello, %s", username));
  }
}