package com.comp6442.route42.api;

import androidx.annotation.NonNull;

import com.comp6442.route42.data.model.Post;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class KNearestNeighbourService extends RestApiService implements Callable<List<Post>> {
  private final int k;
  private final Double lat;
  private final Double lon;

  public KNearestNeighbourService(int k, Double lat, Double lon) {
    this.k = k;
    this.lat = lat;
    this.lon = lon;
  }

  public int getK() {
    return this.k;
  }

  public Double getLat() {
    return this.lat;
  }

  public Double getLon() {
    return this.lon;
  }

  @NonNull
  @Override
  public String toString() {
    return "KNearestNeighbourService{" +
            "k=" + k +
            ", lat=" + lat +
            ", lon=" + lon +
            '}';
  }

  @Override
  public List<Post> call() throws Exception {
    Call<List<Post>> callSync = super.api.getKNearestNeighbors(k, lat, lon);

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
