package com.comp6442.groupproject.data;

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
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    // Create a new view, which defines the UI of the list item
    View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.post_card, viewGroup, false);

    Timber.d("PostAdapter created.");
    return new ViewHolder(view);
  }

  // Replace the contents of a view (invoked by the layout manager)
  @SuppressLint("DefaultLocale")
  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int position) {

    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    viewHolder.materialCardView.setStrokeWidth(5);
    viewHolder.textView1.setText(posts.get(position).getUserName());
    viewHolder.textView2.setText(String.format("Sample text for card #%d", position));
    viewHolder.routeImage.setImageResource(R.drawable.route);

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
    public ImageView userIcon, routeImage;
    public TextView textView1, textView2;
    public MaterialCardView materialCardView;

    public ViewHolder(View view) {
      super(view);
      // Define click listener for the ViewHolder's View
      textView1 = view.findViewById(R.id.post_text1);
      textView2 = view.findViewById(R.id.post_text2);
      userIcon = view.findViewById(R.id.post_user_icon);
      routeImage = view.findViewById(R.id.post_route);
      materialCardView = view.findViewById(R.id.post_card);
    }
  }
}