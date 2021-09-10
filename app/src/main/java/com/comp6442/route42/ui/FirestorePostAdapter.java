package com.comp6442.route42.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.comp6442.route42.R;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.ui.fragment.ProfileFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import timber.log.Timber;

/* Class to feed Cloud Firestore documents into the FirestoreRecyclerAdapter */
public class FirestorePostAdapter extends FirestoreRecyclerAdapter<Post, FirestorePostAdapter.PostViewHolder> {
  private Context context;

  public FirestorePostAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
    super(options);
  }

  @NonNull
  @Override
  public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.post_card, viewGroup, false);

    Timber.d("PostAdapter created.");
    return new PostViewHolder(view);
  }

  @Override
  protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int position, @NonNull Post post) {
    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    viewHolder.materialCardView.setStrokeWidth(5);
    viewHolder.userNameView.setText(post.getUserName());

    // Add listener and navigate to the user's profile on click
    viewHolder.userNameView.setOnClickListener(view -> {
      Fragment fragment = new ProfileFragment();
      Bundle bundle = new Bundle();

      bundle.putString("uid", post.getUid().getId());
      fragment.setArguments(bundle);
      ((AppCompatActivity) viewHolder.itemView.getContext()).getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container_view, fragment)
              .commit();
    });

    // viewHolder.descriptionView.setText("This is a sample text. This is a sample text.");
    if (post.getHashtags().size() > 0)
      viewHolder.hashtagsTextView.setText(String.join(" ", post.getHashtags()));

    // set profile pic
    Timber.i("Fetched post: %s", post);

    // Get reference to the image file in Cloud Storage, download route image, use stock photo if fail
    StorageReference profilePicRef = FirebaseStorageRepository.getInstance().get(post.getProfilePicUrl());
    Glide.with(viewHolder.userIcon.getContext())
            .load(profilePicRef)
            .placeholder(R.drawable.unknown_user)
            .circleCrop()
            .into(viewHolder.userIcon);

    // cache disabled for rendering random images
    Glide.with(viewHolder.routeImage.getContext())
            .load("https://source.unsplash.com/random?w=300")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.route)
            // .fitCenter()
            .centerCrop()
            .into(viewHolder.routeImage);

    // set activity icon
    switch (post.getActivity()) {
      case Run:
        viewHolder.activityIcon.setImageResource(R.drawable.run);
        break;
      case Walk:
        viewHolder.activityIcon.setImageResource(R.drawable.walk);
        break;
      case Cycle:
        viewHolder.activityIcon.setImageResource(R.drawable.cycle);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + post.getActivity());
    }

    Timber.d("OnBindView complete.");
  }

  @Override
  public void onDataChanged() {
    //Called each time there is a new query snapshot.
    Timber.i("breadcrumb");
  }

  @Override
  public void onError(@NonNull FirebaseFirestoreException e) {
    //Handle the error
    Timber.d(e);
  }

  public static class PostViewHolder extends RecyclerView.ViewHolder {
    public ImageView userIcon, routeImage, activityIcon;
    public TextView userNameView, hashtagsTextView, descriptionView;
    public MaterialCardView materialCardView;

    public PostViewHolder(View view) {
      super(view);
      // Define click listener for the ViewHolder's View
      userIcon = view.findViewById(R.id.card_profile_pic);
      routeImage = view.findViewById(R.id.card_main_image);
      activityIcon = view.findViewById(R.id.card_activity_icon);

      userNameView = view.findViewById(R.id.card_username);
      hashtagsTextView = view.findViewById(R.id.card_hashtags);
      descriptionView = view.findViewById(R.id.card_description);

      materialCardView = view.findViewById(R.id.post_card);
    }
  }
}