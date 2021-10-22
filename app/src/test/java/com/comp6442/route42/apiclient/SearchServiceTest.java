package com.comp6442.route42.apiclient;

import com.comp6442.route42.api.RestApiClient;
import com.comp6442.route42.api.SearchService;

import org.junit.Assert;
import org.junit.Test;

import retrofit2.Retrofit;

public class SearchServiceTest {
  protected Retrofit retrofit;
  protected RestApiClient api;

  @Test
  public void searchTest() throws Exception {

    //----Check query sentence but retrofit and api result is randomly changed so check it partially
    SearchService search = new SearchService("#Run");
    // "SearchService{retrofit=retrofit2.Retrofit@40e05db1, api=retrofit2.Retrofit$1@40e6a4fb, query='#Run'}";
    Assert.assertNull(search.call());//no result
    Assert.assertTrue(search.toString().contains("SearchService{"));
    Assert.assertTrue(search.toString().contains("retrofit=retrofit2."));
    Assert.assertTrue(search.toString().contains(", api=retrofit2.Retrofit"));
    Assert.assertTrue(search.toString().contains(", query='#Run'}"));

    SearchService search2 = new SearchService("#Run Test");
    //SearchService{retrofit=retrofit2.Retrofit@4a7a95bb, api=retrofit2.Retrofit$1@63d1ac2c, query='#Run Test'}
    Assert.assertTrue(search2.toString().contains("SearchService{"));
    Assert.assertTrue(search2.toString().contains("retrofit=retrofit2."));
    Assert.assertTrue(search2.toString().contains(", api=retrofit2.Retrofit"));
    Assert.assertTrue(search2.toString().contains(", query='#Run Test'}"));
  }
}
