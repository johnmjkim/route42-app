package com.comp6442.route42.data;

import androidx.lifecycle.LiveData;

import com.comp6442.route42.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

public final class FirebaseAuthLiveData extends LiveData<FirebaseAuth> {
  private static FirebaseAuthLiveData instance = null;
  private static FirebaseAuth mAuth;

  private FirebaseAuthLiveData(FirebaseAuth auth) {
    mAuth = auth;
  }

  public static FirebaseAuthLiveData getInstance() {
    if (FirebaseAuthLiveData.instance == null) {
      FirebaseAuth mAuth = FirebaseAuth.getInstance();
      if (BuildConfig.EMULATOR) {
        try {
          // 10.0.2.2 is the special IP address to connect to the 'localhost' of
          // the host computer from an Android emulator.
          mAuth.useEmulator("10.0.2.2", 9099);
        } catch (IllegalStateException exc) {
          Timber.d(exc);
        }
      }
      FirebaseAuthLiveData.instance = new FirebaseAuthLiveData(mAuth);
    }
    return FirebaseAuthLiveData.instance;
  }

  public FirebaseAuth getAuth() {
    return mAuth;
  }
}