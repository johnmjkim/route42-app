package com.comp6442.route42.utils.apiclient;

import com.comp6442.route42.data.model.Post;

import java.io.IOException;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;

public class PostService extends RestApi implements Callable<Post> {
  private String postId;

  public PostService(String postId) {
    super();
    this.postId = postId;
  }

  @Override
  public Post call() throws Exception {
    Call<Post> callSync = super.api.getPost(this.postId);

    try {
      Response<Post> response = callSync.execute();
      Post post = response.body();
      return post;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
