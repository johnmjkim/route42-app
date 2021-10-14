package com.comp6442.route42.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.route42.R;
import com.comp6442.route42.api.SearchService;
import com.comp6442.route42.data.UserViewModel;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.ui.FirestorePostAdapter;
import com.comp6442.route42.ui.PostAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonSyntaxException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
  private SearchView searchView;
  private NestedScrollView scrollview;
  private RecyclerView recyclerView;
  private PostAdapter adapter;
  // private FirestorePostAdapter firestorePostAdapter;
  private LinearLayoutManager layoutManager;
  private BottomNavigationView bottomNavView;
  private ExecutorService executor = Executors.newSingleThreadExecutor();

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
    getActivity().findViewById(R.id.Btn_Create_Activity).setVisibility(View.VISIBLE);
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Timber.d("breadcrumb");

    if (savedInstanceState != null) {
      Timber.i("Restoring fragment state");
      this.uid = savedInstanceState.getString("uid");
    }

    viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    if (this.uid != null) {
      User user = viewModel.getLiveUser().getValue();

      assert user != null;

      // without firestore post adapter
      Query query = PostRepository.getInstance().getVisiblePosts(user, 20);
      query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
          List<Post> posts = queryDocumentSnapshots.toObjects(Post.class);
          adapter = new PostAdapter(posts, viewModel.getLiveUser().getValue().getId());
          layoutManager = new LinearLayoutManager(getActivity());
          layoutManager.setReverseLayout(false);
          layoutManager.setStackFromEnd(false);

          recyclerView = view.findViewById(R.id.recycler_view);
          recyclerView.setLayoutManager(layoutManager);
          recyclerView.setAdapter(adapter);
          recyclerView.setHasFixedSize(false);
          recyclerView.setNestedScrollingEnabled(false);
          adapter.notifyDataSetChanged();

          initFeed(view);
          initSearch(view, user);
        }
      });

//      // with firestore post adapter
//      FirestoreRecyclerOptions<Post> postsOptions = new FirestoreRecyclerOptions.Builder<Post>()
//              .setQuery(query, Post.class)
//              .build();
//
//      firestorePostAdapter = new FirestorePostAdapter(postsOptions, viewModel.getLiveUser().getValue().getId());
//
//      layoutManager = new LinearLayoutManager(getActivity());
//      layoutManager.setReverseLayout(false);
//      layoutManager.setStackFromEnd(false);
//
//      recyclerView = view.findViewById(R.id.recycler_view);
//      recyclerView.setLayoutManager(layoutManager);
//      recyclerView.setAdapter(firestorePostAdapter);
//      recyclerView.setHasFixedSize(false);
//
//      firestorePostAdapter.startListening();
//
//      Timber.i("PostAdapter bound to RecyclerView with size %d", firestorePostAdapter.getItemCount());
//      query.get().addOnSuccessListener(queryDocumentSnapshots -> Timber.i("%d items found", queryDocumentSnapshots.getDocuments().size()));
//
//      hideSearchOnScroll(view);
//      initSearch(view, user);
    } else {
      Timber.e("uid is null");
    }
  }

  public void initFeed(View view) {
    // hide search view on scroll
    bottomNavView = requireActivity().findViewById(R.id.bottom_navigation_view);

    scrollview = view.findViewById(R.id.feed_scroll_view);
    scrollview.setSmoothScrollingEnabled(true);

    searchView = view.findViewById(R.id.search_view);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy > 0) {
          // scrolling down
          searchView.animate().translationY(-searchView.getHeight()).setDuration(500);
          bottomNavView.animate().translationY(bottomNavView.getHeight()).setDuration(500);
        } else {
          // scrolling up
          searchView.animate().translationY(0).setDuration(500);
          bottomNavView.animate().translationY(0).setDuration(500);
        }
      }
    });
  }

  public void initSearch(View view, User user) {
    // search
    SearchView searchView = view.findViewById(R.id.search_view);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String queryText) {
        if (!queryText.isEmpty()) {
            SearchService api = new SearchService(queryText);
            Timber.i("SearchService created %s", api);
            Future<List<Post>> future = executor.submit(api);
            try {
              List<Post> posts = future.get();
              if (posts != null) {
                Timber.i("Response received from API: %d items", posts.size());
                Timber.d(posts.toString());

                // query via REST API
                adapter.setPosts(posts);
                adapter.notifyDataSetChanged();
//            // query directly using firestore
//            firestorePostAdapter = queryFirestore(user, s);
//            firestorePostAdapter.startListening();
              } else {
                // do nothing, or let the user know there was no hit for the query
              }
            } catch (InterruptedException | ExecutionException | JsonSyntaxException e) {
              Timber.e(e);
            }
          }
        return true;
      }

      // do not react to every text edit
      @Override
      public boolean onQueryTextChange(String queryText) {
        return false;
      }
    });
  }

  public FirestorePostAdapter queryFirestore(User user, String queryText) {
    Query query;
    if (TextUtils.isEmpty(queryText)) {
      query = PostRepository.getInstance().getVisiblePosts(user, 20);
    } else {
      query = PostRepository.getInstance().searchByNamePrefix(user, queryText, 20);
    }
    FirestoreRecyclerOptions<Post> posts = new FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post.class)
            .build();
    FirestorePostAdapter adapter = new FirestorePostAdapter(posts, viewModel.getLiveUser().getValue().getId());
    recyclerView.setAdapter(adapter);
    Timber.i("PostAdapter bound to RecyclerView with size %d for query: %s", adapter.getItemCount(), queryText);
    query.get().addOnSuccessListener(queryDocumentSnapshots -> Timber.i("%d items found", queryDocumentSnapshots.getDocuments().size()));
    return adapter;
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
//    firestorePostAdapter.stopListening();
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