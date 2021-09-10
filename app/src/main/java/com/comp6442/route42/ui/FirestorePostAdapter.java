package com.comp6442.route42.ui;


import static com.comp6442.route42.data.repository.FirebaseStorageRepository.BUFFER_SIZE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.route42.R;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import timber.log.Timber;

/* Class to feed Cloud Firestore documents into the FirestoreRecyclerAdapter */
public class FirestorePostAdapter extends FirestoreRecyclerAdapter<Post, FirestorePostAdapter.PostViewHolder> {

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
    viewHolder.descriptionView.setText("This is a sample text. This is a sample text.");

    // set profile pic
    Timber.i("Fetched post: %s", post);

    FirebaseStorageRepository.getInstance()
            .get(post.getProfilePicUrl())
            .getBytes(BUFFER_SIZE)
            .addOnSuccessListener(bytes -> {
              Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              viewHolder.userIcon.setImageBitmap(bitmap);
              Timber.i("Profile picture fetched");
            }).addOnFailureListener(e -> {
              viewHolder.userIcon.setImageResource(R.drawable.person_photo);
              Timber.d("Could not fetch profile picture: %s", post.getProfilePicUrl());
              Timber.e(e);
            });


    // download route image, use stock photo if fail
    StorageReference pathReference = FirebaseStorageRepository.getInstance().get("images/route.png");

    pathReference.getBytes(BUFFER_SIZE).addOnSuccessListener(bytes -> {
      Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
      viewHolder.routeImage.setImageBitmap(bitmap);
    }).addOnFailureListener(e -> {
      viewHolder.routeImage.setImageResource(R.drawable.route);
      Timber.e(e);
    });

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
  }

  @Override
  public void onError(FirebaseFirestoreException e) {
    //Handle the error
    Timber.d(e);
  }

  public static class PostViewHolder extends RecyclerView.ViewHolder {
    public ImageView userIcon, routeImage, activityIcon;
    public TextView userNameView, activityTextView, descriptionView;
    public MaterialCardView materialCardView;

    public PostViewHolder(View view) {
      super(view);
      // Define click listener for the ViewHolder's View
      userIcon = view.findViewById(R.id.post_user_icon);
      routeImage = view.findViewById(R.id.post_route);
      activityIcon = view.findViewById(R.id.activity_icon);

      userNameView = view.findViewById(R.id.post_username);
      activityTextView = view.findViewById(R.id.activity_label);
      descriptionView = view.findViewById(R.id.post_description);

      materialCardView = view.findViewById(R.id.post_card);
    }
  }
}