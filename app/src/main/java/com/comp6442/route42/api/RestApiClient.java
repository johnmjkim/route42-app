package com.comp6442.route42.api;

import com.comp6442.route42.data.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiClient {

  @GET("post/{id}")
  public Call<Post> getPost(@Path("id") String postId);

  @POST("search/")
  public Call<List<Post>> search(@Body QueryString query);

  @GET("search/knn")
  public Call<List<Post>> getKNearestNeighbors(@Query("k") int k, @Query("lat") Double lat, @Query("lon") Double lon);

  @GET("search/wrn")
  public Call<List<Post>> getWithinRadiusNeighbors(@Query("radius") Double radius, @Query("lat") Double lat, @Query("lon") Double lon);
}