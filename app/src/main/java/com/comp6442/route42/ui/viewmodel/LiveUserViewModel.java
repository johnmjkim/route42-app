package com.comp6442.route42.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import timber.log.Timber;

// ViewModels are independent of configuration changes and are cleared when activity/fragment is destroyed
public class LiveUserViewModel extends UserViewModel {


}