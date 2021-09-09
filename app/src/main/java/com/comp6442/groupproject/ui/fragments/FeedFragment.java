package com.comp6442.groupproject.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.groupproject.BuildConfig;
import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.model.Post;
import com.comp6442.groupproject.ui.FirestorePostAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
  private static final String ARG_PARAM1 = "uid";
  private String uid;
  private FirebaseFirestore firestore;
  private RecyclerView recyclerView;
  private FirestorePostAdapter adapter;
  //  private PostAdapter adapter;
  private LinearLayoutManager layoutManager;

  public FeedFragment() {
    // Required empty public constructor
  }

  public static FeedFragment newInstance(String uid) {
    Timber.d("New instance created with uid %s", uid);
    FeedFragment frag = new FeedFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, uid);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      uid = getArguments().getString(ARG_PARAM1);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @SuppressLint("NotifyDataSetChanged")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // firestore
    this.firestore = FirebaseFirestore.getInstance();
    if (BuildConfig.DEBUG) {
      try {
        this.firestore.useEmulator("10.0.2.2", 8080);
      } catch (IllegalStateException exc) {
        Timber.d(exc);
      }
    }

    // disable caching
    this.firestore.setFirestoreSettings(
            new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
    );

    // restore data
    if (savedInstanceState == null) {
      Timber.d("No previous state to restore from. Checking arguments..");
      Bundle args = getArguments();
      if (args != null) {
        this.uid = args.getString(ARG_PARAM1);
        Timber.d("Received arg: %s", this.uid);
      } else {
        Timber.w("Did not receive argument");
      }
    } else {
      Timber.d("Restoring from previous state: %s", ARG_PARAM1);
      this.uid = savedInstanceState.getString(ARG_PARAM1);
    }

    if (this.uid != null) {
      Query query = this.firestore.collection("posts")
              .orderBy("userName")
              .limit(50);

      FirestoreRecyclerOptions<Post> posts = new FirestoreRecyclerOptions.Builder<Post>()
              .setQuery(query, Post.class)
              .build();

      adapter = new FirestorePostAdapter(posts);
      adapter.notifyDataSetChanged();
      layoutManager = new LinearLayoutManager(getActivity());
      recyclerView = view.findViewById(R.id.recycler_view);
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setAdapter(adapter);
      adapter.startListening();

      Timber.d("PostAdapter bound to RecyclerView with size %d", adapter.getItemCount());
      query.get().addOnSuccessListener(queryDocumentSnapshots -> Timber.i("%d items found", queryDocumentSnapshots.getDocuments().size()));
      Timber.d("PostAdapter bound to RecyclerView with size %d", adapter.getItemCount());
    } else {
      Timber.w("not signed in");
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    adapter.stopListening();
  }

  @Override
  public void onStart() {
    super.onStart();
    Timber.d("Started");
    if (this.uid != null) Timber.d("Feed instance uid = %s", this.uid);
  }

  @Override
  public void onResume() {
    super.onResume();
    Timber.d("Resumed: %s", this.uid);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putString(ARG_PARAM1, this.uid);
    super.onSaveInstanceState(outState);
    Timber.d("Saved instance state");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Timber.d("Destroying instance");
  }
}