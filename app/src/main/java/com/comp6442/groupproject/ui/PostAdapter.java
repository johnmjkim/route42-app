package com.comp6442.groupproject.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.Post;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import timber.log.Timber;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
  private final ArrayList<Post> posts;

  /**
   * Initialize the dataset of the Adapter.
   *
   * @param dataSet String[] containing the data to populate views to be used
   *                by RecyclerView.
   */
  public PostAdapter(ArrayList<Post> dataSet) {
    posts = dataSet;
  }

  // Create new views (invoked by the layout manager)
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewHolder, int viewType) {
    // Create a new view, which defines the UI of the list item
    View view = LayoutInflater.from(viewHolder.getContext())
            .inflate(R.layout.post_card, viewHolder, false);

    Timber.d("PostAdapter created.");
    return new ViewHolder(view);
  }

  // Replace the contents of a view (invoked by the layout manager)
  @SuppressLint("DefaultLocale")
  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int idx) {

    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    viewHolder.materialCardView.setStrokeWidth(5);
    viewHolder.userNameView.setText(posts.get(idx).getUserName());

    viewHolder.routeImage.setImageResource(R.drawable.route);
    viewHolder.descriptionView.setText("This is a sample text. This is a sample text. This is a sample test.");

    switch (posts.get(idx).getActivity()) {
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
        throw new IllegalStateException("Unexpected value: " + posts.get(idx).getActivity());
    }

    Timber.d("OnBindView called.");
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return posts.size();
  }

  /**
   * Provide a reference to the type of views that you are using
   * (custom ViewHolder).
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView userIcon, routeImage, activityIcon;
    public TextView userNameView, activityTextView, descriptionView;
    public MaterialCardView materialCardView;

    public ViewHolder(View view) {
      super(view);
      // Define click listener for the ViewHolder's View
      userIcon = view.findViewById(R.id.post_user_icon);
      activityIcon = view.findViewById(R.id.activity_icon);
      routeImage = view.findViewById(R.id.post_route);
      userNameView = view.findViewById(R.id.post_username);
      activityTextView = view.findViewById(R.id.activity_label);
      descriptionView = view.findViewById(R.id.post_description);
      materialCardView = view.findViewById(R.id.post_card);
    }
  }
}