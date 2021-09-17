package com.comp6442.route42.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.UserViewModel;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.activity.LogInActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

public class OptionsFragment extends Fragment {
  private FirebaseAuth mAuth;
  private FirebaseUser firebaseUser;
  private UserViewModel viewModel;
  private TextView userNameView;
  private SwitchMaterial publicSwitch;
  private MaterialButton submitButton;

  public OptionsFragment() {
    // Required empty public constructor
  }

  public static OptionsFragment newInstance(String param1) {
    Timber.i("New instance created with param %s", param1);
    return new OptionsFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.mAuth = FirebaseAuthLiveData.getInstance().getAuth();
    this.firebaseUser = this.mAuth.getCurrentUser();
    Timber.i("onCreate called with uid %s", firebaseUser.getUid());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_options, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // set view variables
    userNameView = view.findViewById(R.id.options_username);
    publicSwitch = view.findViewById(R.id.is_public);
    submitButton = view.findViewById(R.id.submit_button);

    viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    viewModel.loadLiveUser(firebaseUser.getUid());
    User user = viewModel.getLiveUser().getValue();

    if (user != null) {
      userNameView.setText(user.getUserName());
      publicSwitch.setChecked(user.getIsPublic() == 1);
      submitButton.setOnClickListener(view1 -> {
        user.setUserName((String) userNameView.getText());
        if (publicSwitch.isChecked()) user.setIsPublic(1);
        else user.setIsPublic(0);
        logOut();
        UserRepository.getInstance().setOne(user);
      });
    } else {
      logOut();
    }
  }

  public void logOut() {
    if (mAuth.getCurrentUser() != null) mAuth.signOut();
    Timber.i("Taking user to sign-in screen");
    startActivity(new Intent(getActivity(), LogInActivity.class));
  }
}