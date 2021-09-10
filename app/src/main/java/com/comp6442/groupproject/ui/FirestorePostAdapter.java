package com.comp6442.groupproject.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.Post;
import com.comp6442.groupproject.data.model.User;
import com.comp6442.groupproject.data.repository.FirebaseStorageRepository;
import com.comp6442.groupproject.data.repository.UserRepository;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import timber.log.Timber;

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

    final long ONE_MEGABYTE = 1024 * 1024;

    // set profile pic
    UserRepository.getInstance()
            .getOne(post.getUid().getId())
            .get()
            .addOnFailureListener(error -> {
              Timber.w("Could not obtain profile picture for user: %s", post.getUid());
              Timber.e(error);
            })
            .addOnSuccessListener(snapshot -> {

              User user = snapshot.toObject(User.class);
              if (user != null) {
                FirebaseStorageRepository.getInstance()
                        .get(user.getProfilePicUrl())
                        .getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(bytes -> {
                          Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                          viewHolder.userIcon.setImageBitmap(bitmap);
                          Timber.i("profile picture set");
                        }).addOnFailureListener(e -> {
                  viewHolder.userIcon.setImageResource(R.drawable.person_photo);
                  Timber.w("Profile picture not found: %s", user);
                  Timber.e(e);
                });
              }
            });

    // download route image, use stock photo if fail
    StorageReference pathReference = FirebaseStorageRepository.getInstance().get("images/route.png");
    pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
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