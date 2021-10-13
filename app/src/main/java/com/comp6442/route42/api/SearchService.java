package com.comp6442.route42.api;

import androidx.annotation.NonNull;

import com.comp6442.route42.data.model.Post;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class SearchService extends RestApiService implements Callable<List<Post>> {
  private String query;

  public SearchService(String query) {
    super();
    this.query = query;
  }

  @NonNull
  @Override
  public String toString() {
    return "SearchService{" +
            "retrofit=" + retrofit +
            ", api=" + api +
            ", query='" + query + '\'' +
            '}';
  }

  @Override
  public List<Post> call() throws Exception {
    Call<List<Post>> callSync = super.api.search(new QueryString(query));

//    callSync.enqueue(new Callback<List<Post>>() {
//      @Override
//      public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
//        for(Post post: response.body()) {
//          System.out.println(post.toString());
//        }
//      }
//
//      @Override
//      public void onFailure(Call<List<Post>> call, Throwable t) {
//        Timber.e(t);
//      }
//    });

    try {
      Response<List<Post>> response = callSync.execute();
      Timber.i("Response received: %s", response.toString());

      List<Post> posts = response.body();
      if (posts != null) {
        Timber.i("Response body: %d items", posts.size());
        Timber.i("Response body: %s", posts.toString());
      }
      return posts;
    } catch (IOException e) {
      Timber.e(e);
      return null;
    }
  }
}
