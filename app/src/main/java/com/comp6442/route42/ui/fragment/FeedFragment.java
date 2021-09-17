package com.comp6442.route42.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.route42.R;
import com.comp6442.route42.data.UserViewModel;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.ui.FirestorePostAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
  private UserViewModel viewModel;
  private RecyclerView recyclerView;
  private FirestorePostAdapter adapter;
  private LinearLayoutManager layoutManager;

  public FeedFragment() {
    // Required empty public constructor
  }

  public static FeedFragment newInstance(String param1) {
    Timber.i("New instance created with param %s", param1);
    FeedFragment fragment = new FeedFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      this.uid = getArguments().getString(ARG_PARAM1);
    }
    Timber.i("onCreate called with uid %s", uid);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Timber.d("breadcrumb");

    if (savedInstanceState != null) {
      //Restore the fragment's state here
      Timber.i("Restoring fragment state");
      this.uid = savedInstanceState.getString("uid");
    }

    viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    if (this.uid != null && viewModel != null) {
      User user = viewModel.getLiveUser().getValue();

      assert user != null;

      Query query = PostRepository.getInstance().getVisiblePosts(user, 20);
      FirestoreRecyclerOptions<Post> posts = new FirestoreRecyclerOptions.Builder<Post>()
              .setQuery(query, Post.class)
              .build();

      adapter = new FirestorePostAdapter(posts, viewModel.getLiveUser().getValue().getId());
//      adapter.notifyDataSetChanged();
      layoutManager = new LinearLayoutManager(getActivity());
      layoutManager.setReverseLayout(false);
      layoutManager.setStackFromEnd(false);

      recyclerView = view.findViewById(R.id.recycler_view);
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setAdapter(adapter);
      recyclerView.setHasFixedSize(false);

//      recyclerView.addOnLayoutChangeListener((changedView, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
//        if (oldBottom < bottom) {
//          recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(0), 100);
//        }
//        Timber.i("breadcrumb %d %d", bottom, oldBottom);
//      });
      adapter.startListening();

      Timber.i("PostAdapter bound to RecyclerView with size %d", adapter.getItemCount());
      query.get().addOnSuccessListener(queryDocumentSnapshots -> Timber.i("%d items found", queryDocumentSnapshots.getDocuments().size()));
      SearchView searchView = (SearchView) view.findViewById(R.id.search_view);
      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String s) {

          return false;
        }


        @Override
        public boolean onQueryTextChange(String s) {

          Query query;
          if(s.length()==0||s==null){
            query = PostRepository.getInstance().getVisiblePosts(user,20);
          }
          else {
            query = PostRepository.getInstance().getSearchedPosts(user, s, 20);
          }
          FirestoreRecyclerOptions<Post> posts = new FirestoreRecyclerOptions.Builder<Post>()
                  .setQuery(query, Post.class)
                  .build();
          adapter = new FirestorePostAdapter(posts, viewModel.getLiveUser().getValue().getId());
          recyclerView.setAdapter(adapter);
          adapter.startListening();
          Timber.i("PostAdapter bound to RecyclerView with size %d", adapter.getItemCount());
          query.get().addOnSuccessListener(queryDocumentSnapshots -> Timber.i("%d items found", queryDocumentSnapshots.getDocuments().size()));

          return true;
        }
      });
    } else {
      Timber.e("not signed in");
    }
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      Parcelable state = savedInstanceState.getParcelable("KeyForLayoutManagerState");
      layoutManager.onRestoreInstanceState(state);
      Timber.d("View state restored");
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    Timber.d("breadcrumb");
    if (this.uid != null) Timber.i("Starting feed for uid = %s", this.uid);
  }

  @Override
  public void onStop() {
    super.onStop();
    Timber.d("breadcrumb");
    adapter.stopListening();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(ARG_PARAM1, this.uid);
    if (layoutManager != null) {
      outState.putParcelable("KeyForLayoutManagerState", layoutManager.onSaveInstanceState());
    }
    Timber.i("Saved instance state");
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Timber.d("breadcrumb");
  }

  /* onDetach() is always called after any Lifecycle state changes. */
  @Override
  public void onDetach() {
    super.onDetach();
    Timber.d("breadcrumb");
  }
}