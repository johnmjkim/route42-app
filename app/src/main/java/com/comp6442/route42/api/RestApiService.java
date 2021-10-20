package com.comp6442.route42.api;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.repository.PostRepository;
import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.UnknownHostException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public abstract class RestApiService {
  protected Retrofit retrofit;
  protected RestApiClient api;
  protected final Gson gson = PostRepository.getJsonDeserializer();

  public RestApiService() {
    // if BuildConfig.EMULATOR is True, assumes rest api is also running locally on port 8080 at localhost
    // replace 192.168.... with your own machine's local IP address
    // for Mac, use the command `ifconfig | grep 192.168`
    String url = (BuildConfig.EMULATOR) ? "http://192.168.219.105:8080/" : "http://13.211.169.204:8080/";
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build();
    api = retrofit.create(RestApiClient.class);
  }
}
