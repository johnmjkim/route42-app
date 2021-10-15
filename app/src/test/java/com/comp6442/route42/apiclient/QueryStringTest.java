package com.comp6442.route42.apiclient;
import com.comp6442.route42.api.QueryString;

import org.junit.Assert;
import org.junit.Test;


public class QueryStringTest {
    String query;
    String parameter1 = "field";
    String parameter2 = "type";
    String value1 = "[@field:fieldName]";
    String value2 = "[@Type:type]";
    char symbol = '?';
    int limit = 1;
    QueryString querySample = new QueryString(query, limit);
    @Test
    public void checkQuery() {
        querySample.setQuery(symbol + parameter1 + "=" + value1 + "&" + parameter2 + "=" + value2);
        Assert.assertEquals(querySample.getQuery(),"?field=[@field:fieldName]&type=[@Type:type]");
    }
}