package com.comp6442.route42.api;

import androidx.annotation.NonNull;

public class QueryString {
  private final int limit;
  private String query;

  public QueryString(String query, int limit) {
    this.query = query;
    this.limit = limit;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  @NonNull
  @Override
  public String toString() {
    return "QueryString{" +
            "query='" + query + '\'' +
            ", limit=" + limit +
            '}';
  }
}
