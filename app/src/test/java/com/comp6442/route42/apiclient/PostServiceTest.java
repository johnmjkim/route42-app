package com.comp6442.route42.apiclient;

import com.comp6442.route42.api.PostService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PostServiceTest {
    String postId = "9bbec662-8a7a-4c96-ae90-eb4efcbf8a4f";
    PostService postService;

    @Before
    public void setup() {
        postService = new PostService(postId);
    }

    @Test
    public void callTest() throws Exception {
        Assert.assertEquals(null, postService.call());
    }
}
