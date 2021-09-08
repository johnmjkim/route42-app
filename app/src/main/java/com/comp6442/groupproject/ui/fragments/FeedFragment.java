package com.comp6442.groupproject.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.groupproject.R;
import com.comp6442.groupproject.data.PostAdapter;
import com.comp6442.groupproject.data.model.Activity;
import com.comp6442.groupproject.data.model.Post;
import com.comp6442.groupproject.data.model.TsPoint;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

//  import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
  private static final String ARG_PARAM1 = "uid";
  private String uid;
  private RecyclerView recyclerView;
  private LinearLayoutManager layoutManager;
  private PostAdapter adapter;

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

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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
      ArrayList<Post> posts = new ArrayList<>();
      List<TsPoint> points = Arrays.asList(
              new TsPoint(Timestamp.now(), -33.884633, 151.194464),
              new TsPoint(Timestamp.now(), -33.884889, 151.194453),
              new TsPoint(Timestamp.now(), -33.884986, 151.194464),
              new TsPoint(Timestamp.now(), -33.885154, 151.194378),
              new TsPoint(Timestamp.now(), -33.885371, 151.194288),
              new TsPoint(Timestamp.now(), -33.885517, 151.194197),
              new TsPoint(Timestamp.now(), -33.885645, 151.194150),
              new TsPoint(Timestamp.now(), -33.885826, 151.194059),
              new TsPoint(Timestamp.now(), -33.885981, 151.194022),
              new TsPoint(Timestamp.now(), -33.886171, 151.193905),
              new TsPoint(Timestamp.now(), -33.886343, 151.193809),
              new TsPoint(Timestamp.now(), -33.886533, 151.193761),
              new TsPoint(Timestamp.now(), -33.886666, 151.193702),
              new TsPoint(Timestamp.now(), -33.886644, 151.193657),
              new TsPoint(Timestamp.now(), -33.887127, 151.193300),
              new TsPoint(Timestamp.now(), -33.887265, 151.193205),
              new TsPoint(Timestamp.now(), -33.887477, 151.192983),
              new TsPoint(Timestamp.now(), -33.887722, 151.192733),
              new TsPoint(Timestamp.now(), -33.887639, 151.192455),
              new TsPoint(Timestamp.now(), -33.887542, 151.192149),
              new TsPoint(Timestamp.now(), -33.887454, 151.191854),
              new TsPoint(Timestamp.now(), -33.887357, 151.191549),
              new TsPoint(Timestamp.now(), -33.887283, 151.191243),
              new TsPoint(Timestamp.now(), -33.886914, 151.191165),
              new TsPoint(Timestamp.now(), -33.886743, 151.191159),
              new TsPoint(Timestamp.now(), -33.886517, 151.191182),
              new TsPoint(Timestamp.now(), -33.886337, 151.191221),
              new TsPoint(Timestamp.now(), -33.886116, 151.191371),
              new TsPoint(Timestamp.now(), -33.885844, 151.191393),
              new TsPoint(Timestamp.now(), -33.885488, 151.191499),
              new TsPoint(Timestamp.now(), -33.885220, 151.191543),
              new TsPoint(Timestamp.now(), -33.884902, 151.191927),
              new TsPoint(Timestamp.now(), -33.884851, 151.192433),
              new TsPoint(Timestamp.now(), -33.884814, 151.192944),
              new TsPoint(Timestamp.now(), -33.884741, 151.193389),
              new TsPoint(Timestamp.now(), -33.884741, 151.194017)
      );
      Timestamp endTs = Timestamp.now();
      Timestamp startTs = new Timestamp(endTs.getSeconds() - 500, endTs.getNanoseconds());
      posts.add(new Post("postId1", "uid1", "foo", points, Activity.Cycle, startTs, endTs));
      posts.add(new Post("postId2", "uid1", "foo", points, Activity.Run, startTs, endTs));
      posts.add(new Post("postId3", "uid2", "bar", points, Activity.Run, startTs, endTs));
      posts.add(new Post("postId4", "uid3", "baz", points, Activity.Walk, startTs, endTs));

      adapter = new PostAdapter(posts);
      layoutManager = new LinearLayoutManager(getActivity());
      recyclerView = view.findViewById(R.id.recycler_view);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setAdapter(adapter);
      Timber.d("PostAdapter bound to RecyclerView with size %d", adapter.getItemCount());
    } else {
      Timber.w("not signed in");
    }
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