package com.comp6442.route42.utils.tasks;

import android.content.Context;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.repository.PostRepository;

import timber.log.Timber;

public class TaskCreatePostsDemo extends TaskCreatePosts {
  private int idx = 0;

  public TaskCreatePostsDemo(Context context, int inputRawResourceId) {
    super(context, inputRawResourceId);
  }

  @Override
  protected void createDocuments() {
    createPostsMiniBatch(
            BuildConfig.batchSize,
            Math.min(super.items.size(), BuildConfig.demoPostLimit)
    );
  }

  public void createPostsMiniBatch(int batchSize, int limit) {

    int prevIdx = idx;
    idx += batchSize;
    if (idx + batchSize < limit) {
      PostRepository.getInstance().setMany(super.items.subList(prevIdx, idx));
      Timber.i("Created posts %d - %d", idx, Math.min(super.items.size(), idx + BuildConfig.batchSize));
    } else {
      Thread.currentThread().interrupt();
      Timber.i("Completed demo.");
    }
  }
}