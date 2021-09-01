//package com.comp6442.groupproject.repository;
//
//import android.util.Log;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class UserRepository {
//
//  private static final String TAG = "UserRepository";
//  private static UserRepository instance = null;
//  private FirebaseAuth mAuth;
//
//  private UserRepository(boolean useEmulator) {
//    // Initialize Firebase Auth
//    mAuth = FirebaseAuth.getInstance();
//
//    if (useEmulator) {
//      // 10.0.2.2 is the special IP address to connect to the 'localhost' of
//      // the host computer from an Android emulator.
//      mAuth.useEmulator("10.0.2.2", 9099);
//    }
//  }
//
//  public static UserRepository getRepository(boolean useEmulator) {
//    if (instance == null) instance = new UserRepository(useEmulator);
//    return instance;
//  }
//
//  public FirebaseUser signIn(String email, String password) {
//    mAuth.signInWithEmailAndPassword(email, password);
//    FirebaseUser user = mAuth.getCurrentUser();
//
//    if(user != null) {
//      Log.i(TAG, String.format("Sign in successful: %s", user.getEmail()));
//      return user;
//    } else {
//      // TODO better handling of null
//      Log.w(TAG, String.format("Sign in failed: %s", email));
//      return null;
//    }
//
//
//
//  }
//
////  public FirebaseUser create(String email, String password) {
////
////  }
//}