package com.comp6442.route42.utils.apiclient;

import com.comp6442.route42.data.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

  @GET("post/{id}")
  public Call<Post> getPost(@Path("id") String postId);

  @POST("search/")
  public Call<List<Post>> search(@Body QueryString query);
}