package com.comp6442.route42.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.route42.R;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;
import com.comp6442.route42.ui.adapter.UserListAdapter;
import com.comp6442.route42.ui.viewmodel.UserViewModel;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserListFragment extends Fragment {
  private static final String ARG_PARAM1 = "uid";
  private static final String ARG_PARAM2 = "fieldName";
  private String uid;
  private String fieldName;

  private UserViewModel viewModel;
  private RecyclerView recyclerView;
  private LinearLayoutManager layoutManager;

  public UserListFragment() {
    // Required empty public constructor
  }

  public static UserListFragment newInstance(String param1, String param2) {
    Timber.i("New instance created with param %s", param1);
    UserListFragment fragment = new UserListFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      this.uid = getArguments().getString(ARG_PARAM1);
      this.fieldName = getArguments().getString(ARG_PARAM2);
    }
    Timber.i("onCreate called with uid %s and %s field", uid, fieldName);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_userlist, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Timber.d("breadcrumb");

    if (savedInstanceState != null) {
      Timber.i("Restoring fragment state");
      this.uid = savedInstanceState.getString(ARG_PARAM1);
      this.fieldName = savedInstanceState.getString(ARG_PARAM2);
    }

    viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

    if (this.uid != null) {
      viewModel.loadProfileUser(uid);
      User user = viewModel.getProfileUser().getValue();

      if (user != null) {
        List<User> users = new ArrayList<>();
        List<DocumentReference> usersRef;

        switch (fieldName) {
          case "following":
            usersRef = user.getFollowing();
            break;
          case "followers":
            usersRef = user.getFollowers();
            break;
          case "blocked":
            usersRef = user.getBlocked();
            break;
          default:
            throw new IllegalStateException("Unexpected value: " + fieldName);
        }

        List<DocumentReference> finalUsersRef = usersRef;
        usersRef.forEach(userRef -> {
          String id = userRef.getId();
          if (id.contains("\"")) id = id.replaceAll("^\"|\"$", "");
          Timber.i("ID=%s", id);
          UserRepository.getInstance().getOne(id).get().addOnSuccessListener(
                  snapshot -> {
                    if (snapshot.exists()) {
                      Timber.i(snapshot.toObject(User.class).toString());
                      users.add(snapshot.toObject(User.class));
                    }

                    if (users.size() == finalUsersRef.size()) {
                      UserListAdapter adapter = new UserListAdapter(users);
                      layoutManager = new LinearLayoutManager(getActivity());
                      layoutManager.setReverseLayout(false);
                      layoutManager.setStackFromEnd(false);

                      recyclerView = view.findViewById(R.id.recycler_view_user);
                      recyclerView.setLayoutManager(layoutManager);
                      recyclerView.setAdapter(adapter);
                      recyclerView.setHasFixedSize(false);
                      recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.HORIZONTAL));
                      Timber.i("PostAdapter bound to RecyclerView with size %d", adapter.getItemCount());
                    }
                  });
        });
        String toastText = "";
        if(usersRef.size()==0){
          if(fieldName.equals("followers")){
            toastText = "No " + fieldName;
          }
          else{
            toastText = "Nobody " + fieldName;
          }
        }
        else{
          if(fieldName.equals("followers")){
            toastText = "Total " + usersRef.size() + " " + fieldName;
          }
          else{
            toastText = "Total " + usersRef.size() + " people " + fieldName;
          }

        }
        Toast toast = Toast.makeText(getContext(), toastText,Toast.LENGTH_SHORT);
        toast.show();
      }
    } else {
      Timber.e("uid is null");
    }
  }
}