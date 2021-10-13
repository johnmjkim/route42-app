package com.comp6442.route42.api;

import androidx.annotation.NonNull;

public class QueryString {
  private String query;

  public QueryString(String query) {
    this.query = query;
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
    return this.query;
  }
}
