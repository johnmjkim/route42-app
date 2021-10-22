package com.comp6442.route42.utils.tasks;

import android.content.Context;

import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.repository.PostRepository;

import java.util.Collections;

public class TaskCreatePosts extends DataTask<Post> {
  public TaskCreatePosts(Context context, int inputRawResourceId) {
    super(
            context,
            inputRawResourceId,
            "posts",
            Post.class,
            PostRepository.getInstance(),
            PostRepository.getJsonDeserializer()
    );
    items.forEach(Post::setGeohash);
    Collections.shuffle(items);
  }
}
