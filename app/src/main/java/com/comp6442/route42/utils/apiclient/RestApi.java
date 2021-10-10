package com.comp6442.route42.utils.apiclient;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.repository.PostRepository;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RestApi {
  protected Retrofit retrofit;
  protected APIService api;
  protected final Gson gson = PostRepository.getJsonDeserializer();

  public RestApi() {
    // if BuildConfig.EMULATOR is True, assumes rest api is also running locally on port 8080 at localhost
    String url = (BuildConfig.EMULATOR) ? "http://10.0.2.2:8080/" : "http://13.211.169.204:8080/";
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    
    retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build();
    api = retrofit.create(APIService.class);
  }
}
