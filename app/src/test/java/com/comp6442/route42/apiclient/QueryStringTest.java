package com.comp6442.route42.apiclient;

import com.comp6442.route42.api.QueryString;

import org.junit.Assert;
import org.junit.Test;


public class QueryStringTest {
  String query;
  String parameter1 = "field";
  String parameter2 = "type";
  String[] values1 = new String[]{"[@field:fieldName1]", "[@field:fieldName2]", "[@field:fieldName3]"};
  String[] values2 = new String[]{"[@Type:type1]", "[@Type:type2]", "[@Type:type3]"};
  char symbol = '?';
  int limit = 3;
  QueryString querySample = new QueryString(query, limit);

  @Test
  public void checkQuery() {
    for (int i = 0; i < limit; i++) {
      querySample.setQuery(symbol + parameter1 + "=" + values1[i] + "&" + parameter2 + "=" + values2[i]);
      Assert.assertEquals(querySample.getQuery(), String.format("?field=[@field:fieldName%d]&type=[@Type:type%d]", i + 1, i + 1));
    }
  }

  @Test
  public void checkQueryString() {
    for (int i = 0; i < limit; i++) {
      querySample.setQuery(symbol + parameter1 + "=" + values1[i] + "&" + parameter2 + "=" + values2[i]);
      Assert.assertEquals(String.valueOf(querySample), "QueryString{query='" + querySample.getQuery() + "', limit=" + limit + "}");
    }
  }
}