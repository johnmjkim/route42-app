package com.comp6442.route42.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.comp6442.route42.R;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.ui.fragment.ProfileFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import timber.log.Timber;

/* Class to feed Cloud Firestore documents into the FirestoreRecyclerAdapter */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
  private final List<User> users;

  public UserListAdapter(List<User> users) {
    this.users = users;
    Timber.i(users.toString());
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row, viewGroup, false);
    Timber.d("PostAdapter created.");
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
    User user = users.get(position);
    viewHolder.userNameView.setText(user.getUserName());
    viewHolder.userNameView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Fragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();

        bundle.putString("uid", user.getId());
        fragment.setArguments(bundle);
        ((FragmentActivity) viewHolder.itemView.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_view, fragment)
                .addToBackStack(this.getClass().getCanonicalName())
                .commit();
      }
    });

    if (user.getProfilePicUrl().startsWith("http")) {
      Glide.with(viewHolder.userIcon.getContext())
              .load(user.getProfilePicUrl())
              .diskCacheStrategy(DiskCacheStrategy.NONE)
              .skipMemoryCache(false)
              .circleCrop()
              .into(viewHolder.userIcon);
    } else {
      // Get reference to the image file in Cloud Storage, download route image, use stock photo if fail
      StorageReference profilePicRef = FirebaseStorageRepository.getInstance().get(user.getProfilePicUrl());

      Glide.with(viewHolder.userIcon.getContext())
              .load(profilePicRef)
              .placeholder(R.drawable.unknown_user)
              .diskCacheStrategy(DiskCacheStrategy.NONE)
              .skipMemoryCache(false)
              .circleCrop()
              .into(viewHolder.userIcon);
    }

    Timber.i("Fetched user: %s", user);
    Timber.d("OnBindView complete.");
  }

  @Override
  public int getItemCount() {
    return users.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public MaterialCardView materialCardView;
    public ImageView userIcon;
    public TextView userNameView;

    public ViewHolder(View view) {
      super(view);
      materialCardView = view.findViewById(R.id.user_row_card);
      userIcon = view.findViewById(R.id.user_row_profile_pic);
      userNameView = view.findViewById(R.id.user_row_username);
    }
  }
}