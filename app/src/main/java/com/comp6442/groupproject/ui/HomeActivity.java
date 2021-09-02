package com.comp6442.groupproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comp6442.groupproject.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
  private static final String TAG = "HomeActivity";

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
    // key must match
    String uid = intent.getStringExtra("uid");
    Task<QuerySnapshot> task = UserRepository.getInstance().getUser(uid);
    Log.i(TAG, uid);
    task.addOnCompleteListener(task2 -> {
      QuerySnapshot snapshot = task2.getResult();
      DocumentSnapshot documentSnapshot = snapshot.getDocuments().get(0);
      User user = new User(
              (String) Objects.requireNonNull(documentSnapshot.get("uid")),
              (String) Objects.requireNonNull(documentSnapshot.get("email"))
      );

      Log.i(TAG, String.format("User successfully fetched: %s", user.getEmail()));
      TextView txtView = findViewById(R.id.usernameHome);
      txtView.setText(String.format("Hello, %s", user.getUid()));
    });
  }
}